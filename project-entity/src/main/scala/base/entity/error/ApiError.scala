/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 11:05 PM
 */

package base.entity.error

import base.entity.api.ApiErrorCodes.ErrorCode
import base.entity.api.ApiStrings._
import com.wordnik.swagger.annotations.{ApiModel, ApiModelProperty}
import spray.http.StatusCode

import scala.annotation.meta.field

/**
 * API describable Error
 * @author rconrad
 */
// format: OFF
@ApiModel(description = errorObject)
case class ApiError private[error] (
  @(ApiModelProperty @field)(required = true,   value = errorDescCodeDesc)  command: Option[String] = None,
  @(ApiModelProperty @field)(required = true,   value = errorStatusDesc)    status: StatusCode,
  @(ApiModelProperty @field)(required = false,  value = errorDescCodeDesc)  code: Option[ErrorCode] = None,
  @(ApiModelProperty @field)(required = false,  value = errorParamDesc)     param: Option[String] = None,
  @(ApiModelProperty @field)(required = true,   value = errorMessageDesc)   message: String,
  @(ApiModelProperty @field)(required = true,   value = errorUniqueIdDesc)  uniqueId: String,
  @(ApiModelProperty @field)(required = false,  value = errorUniqueIdDesc)  debug: Option[String] = None
)
// format: ON
