/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 9:57 PM
 */

package base.entity.user.model

import base.common.lib.Genders.Gender
import base.entity.ApiStrings.User._
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
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  name: String,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  gender: Gender,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  phone: String) {
  // format: ON

}
