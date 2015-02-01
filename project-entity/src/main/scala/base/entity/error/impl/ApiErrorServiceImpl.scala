/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 11:09 AM
 */

package base.entity.error.impl

import base.common.lib.{Tryo, Encoding}
import base.common.logging.Loggable
import base.common.service.ServiceImpl
import base.entity.api.ApiErrorCodes.ErrorCode
import base.entity.command.model.CommandModel
import base.entity.error.ApiErrorService
import base.entity.error.model.ApiError
import base.entity.error.model.impl.ApiErrorImpl
import base.entity.json.JsonFormats
import com.google.common.hash.Hashing
import org.json4s.native.Serialization
import spray.caching.LruCache
import spray.http.{StatusCode, StatusCodes}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * {{ Describe the high level purpose of ApiErrorServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class ApiErrorServiceImpl(debugMode: Boolean) extends ServiceImpl with ApiErrorService {

  private implicit val formats = JsonFormats.withModels

  private val CACHE_SIZE = 1000
  private val cache = LruCache[String](maxCapacity = CACHE_SIZE, initialCapacity = CACHE_SIZE)

  /**
   * Default the uniqueId to be seeded from the message returned to the user
   * NB: this is only a good idea if your message is immutable; if it has
   * dynamic content the uniqueId will be pretty useless for correlation
   * (though it will still help you find specific incidents in the logs)
   */
  def badRequest(message: String) =
    statusCode(message, StatusCodes.BadRequest)

  def statusCode(message: String, status: StatusCode) =
    statusCodeSeed(message, status, s"$status, $message")

  def statusCodeSeed(message: String, status: StatusCode, uniqueIdSeed: String) =
    full(message, status, None, None, uniqueIdSeed)

  def errorCode(message: String, status: StatusCode, code: ErrorCode) =
    full(message, status, Option(code), None, s"$status, $code")

  def errorCodeSeed(message: String, status: StatusCode, code: ErrorCode, uniqueIdSeed: String) =
    full(message, status, Option(code), None, uniqueIdSeed)

  def throwable(message: String, status: StatusCode, throwable: Throwable) =
    statusCodeSeed(message, status, s"$status, ${Loggable.stackTraceToString(throwable)}")

  def full(message: String,
            status: StatusCode,
            code: Option[ErrorCode],
            param: Option[String],
            uniqueIdSeed: String) = {
    val debugString = debugMode match {
      case true => Option(uniqueIdSeed)
      case false => None
    }
    val apiError = ApiErrorImpl(
      command = None,
      status = status,
      code = code,
      message = message,
      param = param,
      uniqueId = getUniqueId(uniqueIdSeed),
      debug = debugString)
    debug("%s: %s", apiError, uniqueIdSeed)
    apiError
  }

  def toJson(apiError: ApiError) = {
    val command = CommandModel(apiError)
    Tryo(Serialization.write(command)) match {
      case Some(json) => json
      case None =>
        error("failed to serialize %s", apiError)
        ""
    }
  }

  /**
   * Create a uniquely identifiable handle for an error based on a string seed value
   */
  def getUniqueId(seed: String) = Await.result(cache(seed) {
    Hashing.md5.hashString(seed, Encoding.CHARSET_UTF8).toString
  }, 10.millis)

}
