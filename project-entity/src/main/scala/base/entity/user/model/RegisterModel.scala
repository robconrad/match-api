/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 9:36 AM
 */

package base.entity.user.model

import base.entity.api.ApiStrings.User._
import base.entity.api.ApiVersions.ApiVersion
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = createRequestDesc)
case class RegisterModel(
  @(ApiModelProperty @field)(required = true, value = emailDesc)     apiVersion: ApiVersion,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  phone: String) {
  // format: ON

}
