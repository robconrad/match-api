/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 10:56 AM
 */

package base.rest.route

import base.common.logging.Loggable
import base.entity.api.ApiStrings
import base.entity.error.{ApiErrorService, ApiException}
import base.entity.perm.PermException
import org.json4s.native.Serialization
import spray.http.StatusCodes._
import spray.http.{ ContentType, HttpEntity, MediaTypes, StatusCodes }
import spray.httpx.Json4sSupport
import spray.routing.{ ExceptionHandler, HttpService, MalformedRequestContentRejection, RejectionHandler }
import spray.util.LoggingContext

/**
 * Separates out the OOB routing event handlers so that they can be mixed in to tests.
 * @author rconrad
 */
private[rest] trait RoutingHandlers extends HttpService with Json4sSupport with Loggable {

  private val errorPrefix = "The request content was malformed: "

  /**
   * Overrides for specific rejection handlers
   */
  private val customRejectionHandlers = RejectionHandler.apply {
    case MalformedRequestContentRejection(msg, _) :: _ =>
      // we drop all lines but the first from content rejections since they are meaningless to
      //  API users and may inadvertently expose internals. (would be nice to catch this
      //  at the source but since that's deep in json4s it's not happening)
      val errorParts = msg.split("\n")
      debug(errorPrefix + errorParts.mkString(". "))
      super[HttpService].complete(BadRequest, errorPrefix + errorParts.head)
    case x => RejectionHandler.Default(x)
  }

  /**
   * Rather than responding with a standard text body rejection, respond with a json object describing errors
   *
   * NOTA BENE: After a great deal of debugging I found that the rejections handler
   *  ABSOLUTELY MUST be on the top level actor to catch every possible rejection
   *  (particularly errors in parsing or extracting JSON). This must be here.
   */
  implicit val customRejectionHandler = RejectionHandler {
    case rejections =>
      compressResponse() {
        mapHttpResponse { response =>
          val asString = response.entity.asString
          val unquoted = asString.charAt(0) == '"' && asString.charAt(asString.length - 1) == '"' match {
            case true  => asString.substring(1, asString.length - 1)
            case false => asString
          }
          val noNewlines = unquoted.replace("\n", " ")
          val errorString = ApiErrorService().toJson(ApiErrorService().statusCode(noNewlines, response.status))
          response
            .withEntity(HttpEntity(ContentType(MediaTypes.`application/json`), errorString))
            .withHeaders(RoutingActor.corsHeaders)
        } {
          customRejectionHandlers(rejections)
        }
      }
  }

  /**
   * See notes for customRejectionHandler above
   */
  implicit def customExceptionHandler(implicit log: LoggingContext): ExceptionHandler = ExceptionHandler {
    case t: Throwable =>
      compressResponse() {
        respondWithHeaders(RoutingActor.corsHeaders) {
          requestUri { uri =>
            error("Request threw ", t)
            val status = t.isInstanceOf[ApiException] match {
              case true  => t.asInstanceOf[ApiException].status
              case false => StatusCodes.InternalServerError
            }
            val apiError = ApiErrorService().throwable(ApiStrings.serverErrorCodeDesc, status, t)
            respondWithStatus(StatusCodes.InternalServerError).apply { ctx =>
              ctx.complete(apiError)
            }
          }
        }
      }
  }

}
