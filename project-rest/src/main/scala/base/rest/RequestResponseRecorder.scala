/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.rest

import base.common.logging.Loggable
import base.common.time.DateTimeHelper
import base.entity.json.JsonFormats
import org.json4s.JsonAST.JString
import org.json4s.JsonDSL._
import org.json4s.ParserUtil.ParseException
import org.json4s.native.Serialization
import org.json4s.{ Extraction, JObject, JValue, MappingException }
import spray.http.{ HttpRequest, HttpResponse }
import spray.routing.{ Rejected, Rejection, TransformationRejection }

/**
 * Records Spray Request/Response pairs to a durable json log
 *
 * NOTE: This is EXPERIMENTAL - it is known to poorly record requests in some instances and the json
 *  format is not kept completely consistent. It is turned on so that we can see what results look like
 *  in production, but it should not be considered production itself.
 *
 * Coverage is off because this feature is experimental and not rigorously tested.
 *
 * @author rconrad
 */
// $COVERAGE-OFF$
private[rest] object RequestResponseRecorder extends Loggable with DateTimeHelper {

  implicit val formats = JsonFormats.withHttpData

  case class RequestRecordField(format: String,
                                `type`: String,
                                value: JValue)

  case class RequestRecord(format: String,
                           uri: String,
                           time: String,
                           request: RequestRecordField,
                           response: RequestRecordField)

  val formatJson = "json"
  val formatString = "string"

  /**
   * Convert request and response to extracted JValues if possible, but on exception
   *  just treat them as strings and mark the format as such in the log
   */
  def apply(request: HttpRequest, response: Any) {
    val requestRecord = composeRequest(request)
    val responseRecord = composeResponse(response)
    val uri = request.uri.toRelative.toString()
    val time: String = now
    val record = RequestRecord(formatJson, uri, time, requestRecord, responseRecord)

    lazy val fallbackRecord: JObject =
      ("format" -> formatString) ~
        ("uri" -> uri) ~
        ("time" -> time) ~
        ("request" -> requestRecord.toString) ~
        ("response" -> responseRecord.toString)

    val json = try {
      Serialization.write(record)
    } catch {
      case _: ParseException | _: MappingException =>
        error(s"failed to write record to json: ${stripNewlines(fallbackRecord.toString)}")
        Serialization.write(fallbackRecord)
    }

    RestRequestRecorder(json)
  }

  /**
   * Attempt to compose HttpRequest into a RequestRecordField as json, fallback to as string
   */
  private def composeRequest(request: HttpRequest) = {
    val typ = request.getClass.getSimpleName
    lazy val recordAsString = RequestRecordField(formatString, typ, request.toString)
    try {
      RequestRecordField(formatJson, typ, Extraction.decompose(request))
    } catch {
      case _: ParseException | _: MappingException =>
        warn(s"failed to decompose request to jvalue: ${stripNewlines(recordAsString.toString)}")
        recordAsString
    }
  }

  /**
   * Attempt to compose a response of whatever type into a RequestRecordField as json, fallback to as string
   */
  private def composeResponse(response: Any) = {
    val typ = response.getClass.getSimpleName
    lazy val recordAsString = RequestRecordField(formatString, typ, response.toString)
    try {
      response match {
        case response: HttpResponse => RequestRecordField(formatJson, typ, Extraction.decompose(response))
        case Rejected(rejections)   => RequestRecordField(formatJson, typ, composeRejections(rejections))
        case response               => recordAsString
      }
    } catch {
      case _: ParseException | _: MappingException =>
        warn(s"failed to decompose response to jvalue: ${stripNewlines(recordAsString.toString)}")
        recordAsString
    }
  }

  /**
   * Attempt to convert rejections to json
   *  Skip TransformationRejections (which get subsumed into standard rejections anyway)
   */
  private def composeRejections(rejections: List[Rejection]): JValue = {
    rejections.filterNot(_.isInstanceOf[TransformationRejection]).map { rejection =>
      lazy val rejectionAsString = rejection.toString
      try {
        Extraction.decompose(rejection)
      } catch {
        case _: ParseException | _: MappingException =>
          warn(s"failed to decompose rejection to jvalue: ${stripNewlines(rejectionAsString)}")
          JString(rejectionAsString)
      }
    }
  }

  /**
   * Special logger that writes these events to rest-requests.json
   */
  private object RestRequestRecorder extends Loggable {
    def apply(s: String) {
      val output = stripNewlines(s)
      // This is a piece of shit hack, but I just don't have time to blank out
      //  passwords properly so all log events with "password" get dropped.
      //  Note that api keys will still get logged...
      if (!output.contains("password")) {
        info(output)
      }
    }
  }

  private def stripNewlines(s: String) = s.replace("\n", "\\n")

}
