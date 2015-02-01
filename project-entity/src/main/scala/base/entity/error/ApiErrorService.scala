/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 3:00 PM
 */

package base.entity.error

import base.common.service.{ Service, ServiceCompanion }
import base.entity.api.ApiErrorCodes.ErrorCode
import base.entity.error.model.ApiError
import spray.http.StatusCode

/**
 * CRUD, etc.
 * @author rconrad
 */
trait ApiErrorService extends Service {

  final val serviceManifest = manifest[ApiErrorService]

  def badRequest(message: String): ApiError

  def statusCode(message: String, status: StatusCode): ApiError

  def statusCodeSeed(message: String, status: StatusCode, uniqueIdSeed: String): ApiError

  def errorCode(message: String, status: StatusCode, code: ErrorCode): ApiError

  def errorCodeSeed(message: String, status: StatusCode, code: ErrorCode, uniqueIdSeed: String): ApiError

  def throwable(message: String, status: StatusCode, throwable: Throwable): ApiError

  def full(message: String,
           status: StatusCode,
           code: Option[ErrorCode],
           param: Option[String],
           uniqueIdSeed: String): ApiError

  def toJson(apiError: ApiError): String

}

object ApiErrorService extends ServiceCompanion[ApiErrorService]
