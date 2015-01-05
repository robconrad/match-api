/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 10:48 PM
 */

package base.entity.error

import base.common.lib.{ Encoding, Dispatchable }
import base.common.logging.Loggable
import base.entity.ApiErrorCodes.ErrorCode
import base.entity.ApiStrings._
import com.google.common.hash.Hashing
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }
import spray.caching.LruCache
import spray.http.{ StatusCode, StatusCodes }

import scala.annotation.meta.field
import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * API describable Error
 * @author rconrad
 */
// format: OFF
@ApiModel(description = errorObject)
case class ApiError(
  @(ApiModelProperty @field)(required = true,   value = errorDescCodeDesc)  cmd: String = "apiError",
  @(ApiModelProperty @field)(required = true,   value = errorDescCodeDesc)  errorCmd: String,
  @(ApiModelProperty @field)(required = true,   value = errorStatusDesc)    status: Int,
  @(ApiModelProperty @field)(required = false,  value = errorDescCodeDesc)  code: Option[String],
  @(ApiModelProperty @field)(required = false,  value = errorParamDesc)     param: Option[String],
  @(ApiModelProperty @field)(required = true,   value = errorMessageDesc)   message: String,
  @(ApiModelProperty @field)(required = true,   value = errorUniqueIdDesc)  uniqueId: String
)
// format: ON

object ApiError extends Dispatchable with Loggable {

  private val CACHE_SIZE = 1000
  private val cache = LruCache[String](maxCapacity = CACHE_SIZE, initialCapacity = CACHE_SIZE)

  implicit def statusCode2Int(s: StatusCode) = s.intValue

  /**
   * Default the uniqueId to be seeded from the message returned to the user
   *  NB: this is only a good idea if your message is immutable; if it has
   *      dynamic content the uniqueId will be pretty useless for correlation
   *      (though it will still help you find specific incidents in the logs)
   */
  def apply(message: String): ApiError =
    apply(message, StatusCodes.BadRequest)

  def apply(message: String, code: ErrorCode, uniqueIdSeed: String): ApiError =
    apply(message, StatusCodes.BadRequest, Option(code), None, uniqueIdSeed)

  def apply(message: String, status: StatusCode): ApiError =
    apply(message, status, s"$status, $message")

  def apply(message: String, status: StatusCode, uniqueIdSeed: Throwable): ApiError =
    apply(message, status, s"$status, $uniqueIdSeed")

  def apply(message: String, status: StatusCode, uniqueIdSeed: String): ApiError =
    apply(message, status, None, None, uniqueIdSeed)

  def apply(message: String,
            status: StatusCode,
            code: Option[ErrorCode],
            param: Option[String],
            uniqueIdSeed: String): ApiError = {
    val error = apply(
      status = status,
      errorCmd = "errorCmd field not yet implemented",
      code = code.map(_.toString),
      param = None,
      message = message,
      uniqueId = getUniqueId(uniqueIdSeed))
    debug("%s: %s", error, uniqueIdSeed)
    error
  }

  /**
   * Create a uniquely identifiable handle for an error based on a string seed value
   */
  def getUniqueId(seed: String) = Await.result(cache(seed) {
    Hashing.md5.hashString(seed, Encoding.CHARSET_UTF8).toString
  }, 10.millis)

}
