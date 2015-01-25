/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 11:45 AM
 */

package base.entity.user.model

import java.util.UUID

import base.common.lib.Genders.Gender
import base.entity.api.ApiStrings
import ApiStrings.User._
import base.entity.api.ApiVersions.ApiVersion
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = createRequestDesc)
// todo convert to interface for mocking
case class VerifyModel(
  @(ApiModelProperty @field)(required = true, value = emailDesc)     apiVersion: ApiVersion,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  name: Option[String],
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  gender: Option[Gender],
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  phone: String,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  deviceUuid: UUID,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  code: String) {
  // format: ON

}
