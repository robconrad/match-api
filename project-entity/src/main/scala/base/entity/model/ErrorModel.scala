/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 9:57 PM
 */

package base.entity.model

import base.entity.ApiStrings.User._
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = createRequestDesc)
case class ErrorModel(
  @(ApiModelProperty @field)(required = true, value = emailDesc)    errorStatus: Int,
  @(ApiModelProperty @field)(required = true, value = emailDesc)    errorCode: Option[String],
  @(ApiModelProperty @field)(required = true, value = passwordDesc) errorMessage: Option[String]) {
  // format: ON

}

