/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 11:00 AM
 */

package base.entity.error.model

import base.entity.api.ApiErrorCodes._
import base.entity.api.ApiStrings._
import base.entity.error.model.impl.ApiErrorImpl
import base.entity.model.{Model, ModelCompanion}
import com.wordnik.swagger.annotations.{ApiModel, ApiModelProperty}
import spray.http.StatusCode

import scala.annotation.meta.field

/**
 * API describable Error
 * @author rconrad
 */
// format: OFF
@ApiModel(description = errorObject)
trait ApiError extends Model {

  @(ApiModelProperty @field)(required = true,   value = errorDescCodeDesc) def command: Option[String]
  @(ApiModelProperty @field)(required = true,   value = errorStatusDesc)   def status: StatusCode
  @(ApiModelProperty @field)(required = false,  value = errorDescCodeDesc) def code: Option[ErrorCode]
  @(ApiModelProperty @field)(required = false,  value = errorParamDesc)    def param: Option[String]
  @(ApiModelProperty @field)(required = true,   value = errorMessageDesc)  def message: String
  @(ApiModelProperty @field)(required = true,   value = errorUniqueIdDesc) def uniqueId: String
  @(ApiModelProperty @field)(required = false,  value = errorUniqueIdDesc) def debug: Option[String]

}
// format: ON

object ApiError extends ModelCompanion[ApiError, ApiErrorImpl]
