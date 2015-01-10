/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 2:42 PM
 */

package base.entity.device.model

import java.util.UUID

import base.entity.api.ApiStrings
import ApiStrings.User._
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = createRequestDesc)
case class DeviceModel(
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  uuid: UUID,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  model: String,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  cordova: String,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  platform: String,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  version: String,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  name: String) {
  // format: ON

}
