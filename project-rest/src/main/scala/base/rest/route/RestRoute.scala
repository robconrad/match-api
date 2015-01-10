/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 2:42 PM
 */

package base.rest.route

import base.entity.api.ApiStrings
import base.entity.error.ApiError
import base.entity.json.JsonFormats
import base.rest.Endpoint
import base.rest.Locations._
import org.json4s.DefaultFormats
import spray.http.StatusCodes
import spray.httpx.Json4sSupport
import spray.httpx.marshalling.{ ToResponseMarshallable, ToResponseMarshaller }
import spray.routing.AuthenticationFailedRejection.{ CredentialsMissing, CredentialsRejected }
import spray.routing._

import scala.concurrent.Future
import scala.util.{ Failure, Success }

/**
 * Base class for all /rest/ routes, provides rest-specific directives
 * @author rconrad
 */
private[rest] trait RestRoute extends BaseRoute {

  override implicit val json4sFormats = JsonFormats.withEnumsAndFields

  /**
   * Endpoints supported by this RestRoute, will be used to generate OPTIONS support for CORS
   * @return
   */
  def endpoints: List[Endpoint]

  /**
   * Finalize parent routes so that we can guarantee CORS routes are always included when endpoints are present
   */
  final def routes = corsRoutes match {
    case Some(corsRoutes) => corsRoutes ~ restRoutes
    case _                => restRoutes
  }

  /**
   * The primary characteristic of a route class is that it defines a route.
   */
  def restRoutes: Route

  /**
   * Convert provided endpoints to routes that accept OPTIONS requests for CORS support
   */
  final def corsRoutes: Option[Route] = {
    val routes = endpoints.map { endpoint =>
      path(endpoint) {
        corsOptions
      }
    }
    routes.size match {
      case 0 => None
      case _ => Option(routes.reduce((a, b) => a ~ b))
    }
  }

  /**
   * Combined authentication and authorization function
   *  Unified across User-based and Key-based authentication - User takes precedence over Key, SECRET over PUBLISHABLE
   *  Restricts auth type to AuthTypeSet and restricts to contexts that have perm
   *
   * User authenticated requests expect a standard Authorization header (ref. spray docs)
   *
   * Key authenticated requests expect one of the custom headers to be supplied with the key as the value, e.g.
   *  X-Base-Key: MYKEYHERE
   */
//  def auth(authTypes: AuthTypeSet, perms: Perm*): Directive1[AuthContext] = {
//    extract { ctx =>
//      // format: OFF
//      val headers                 = ctx.request.headers
//      val userAuth                = headers.find(_.is(Authorization.lowercaseName))
//      val keyAuth                 = headers.find(_.is(ApiKeyTypes.API.lowercaseName))
//      val contextParams           = AuthContextParams()
//      // format: ON
//
//      val result: Future[Either[Any, Option[AuthContext]]] = (userAuth, keyAuth) match {
//        case (Some(Authorization(BasicHttpCredentials(user, pass))), _) =>
//          AuthService().authByUser(user, pass, contextParams).map(Right(_))
//        case (_, Some(keyAuth)) =>
//          AuthService().authByKey(keyAuth.value, contextParams).map(Right(_))
//        case _ =>
//          Future.successful(Left())
//      }
//      result
//    }.flatMap(onComplete(_).flatMap(validateAuthContext(authTypes, perms, _)))
//  }

  /**
   * Decide whether the AuthContext produced by the provided credentials satisfies the requirements of this request
   */
//  private def validateAuthContext(authTypes: AuthTypeSet,
//                                  perms: Seq[Perm],
//                                  authCtx: Try[Either[Any, Option[AuthContext]]]): Directive1[AuthContext] =
//    authCtx match {
//      case Success(Right(Some(authCtx))) =>
//        val authTypeExpected = authTypes.contains(authCtx.authType)
//        val authHasPerms = perms.map(authCtx.has).reduceLeft(_ && _)
//        authTypeExpected && authHasPerms match {
//          case true => provide(authCtx)
//          case false =>
//            debug("rejecting auth (authTypeExpected: %s, authHasPerms: %s) with credentials %s and required perms %s",
//              authTypeExpected, authHasPerms, authCtx, perms)
//            rejectAuthCredentialsNotAuthorized.toDirective
//        }
//      case Success(Right(None)) =>
//        debug("auth credentials did not authenticate")
//        rejectAuthCredentialsRejected.toDirective
//      case Success(Left(a)) =>
//        debug("auth credentials missing")
//        rejectAuthCredentialsMissing.toDirective
//      case x =>
//        debug("auth credentials failed with %s", x)
//        rejectAuthCredentialsRejected.toDirective
//    }

  /**
   * Issue a rejection based on credentials
   */
  private def rejectAuthCredentialsMissing = reject(AuthenticationFailedRejection(CredentialsMissing, List()))
  private def rejectAuthCredentialsRejected = reject(AuthenticationFailedRejection(CredentialsRejected, List()))
  private def rejectAuthCredentialsNotAuthorized = reject(AuthorizationFailedRejection)

  /**
   * Explicitly block the use of standard complete to ensure we always respond with CORS headers, etc.
   */
  override def complete: (⇒ ToResponseMarshallable) ⇒ StandardRoute = marshallable ⇒ new StandardRoute {
    def apply(ctx: RequestContext): Unit = throw new Exception("do not use bare complete() in RestRoutes - " +
      "always use completeEither because it adds the proper headers, logging, etc.")
  }

  /**
   * Facility for completing a request that may have generated errors with a Future response
   */
  def completeEither[T](response: Future[Either[ApiError, T]])(implicit m: ToResponseMarshaller[T]): Route =
    new StandardRoute {
      def apply(ctx: RequestContext): Unit = response.onComplete {
        case Success(response) => completeEither(response).apply(ctx)
        case Failure(e)        => throw e
      }
    }

  /**
   * Facility for completing a request that may have generated errors. Typically routes
   * will call into services which will respond with Either[Errors, RequestedData] allowing
   * them the opportunity to attach multiple and specific errors to any request.
   */
  def completeEither[T](response: Either[ApiError, T])(implicit m: ToResponseMarshaller[T]): Route =
    response match {
      case Right(response) => completeResponse(response)
      case Left(error)     => completeError(error)
    }

  /**
   * Given a response, complete the request with CORS headers
   */
  def completeResponse[T](response: T)(implicit m: ToResponseMarshaller[T]) =
    compressResponse() {
      respondWithHeaders(RoutingActor.corsHeaders).apply { ctx =>
        ctx.complete(response)
      }
    }

  /**
   * Given a list of errors and status code, complete the request with CORS headers --
   *  Except for Unauthorized errors, which instead give the same rejection as failure to authenticate
   *  so that it is not possible to tell at what stage it was determined that the credentials are unauthorized
   */
  def completeError(error: ApiError) =
    compressResponse() {
      error.status match {
        case StatusCodes.Unauthorized => rejectAuthCredentialsRejected
        case status =>
          respondWithStatus(status).apply {
            respondWithHeaders(RoutingActor.corsHeaders).apply { ctx =>
              ctx.complete(error)
            }
          }
      }
    }

  /**
   * Convenience directive for not yet implemented api methods
   */
  def completeNotImplemented = completeError {
    ApiError(ApiStrings.notYetImplementedNote, StatusCodes.NotImplemented)
  }

  /**
   * Convenience directive for completing requests that require no response body
   */
  def completeEmpty = completeResponse {
    RestRoute.emptyResponse
  }

  /**
   * CORS requests will ping the server with an options request before initiating,
   * so we always respond to options at with an empty body and the CORS headers
   */
  def corsOptions = options {
    completeEmpty
  }

  /**
   * Directive requiring the first path segment be /rest-docs/
   */
  def pathBaseTop = pathPrefix(REST)

}

private[rest] object RestRoute extends Json4sSupport {

  implicit def json4sFormats = DefaultFormats

  // pre-marshalled empty response available to any endpoint that doesn't need to provide specific data on success
  val emptyResponse: ToResponseMarshallable = Map[String, String]()

}
