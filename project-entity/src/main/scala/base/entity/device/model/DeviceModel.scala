/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:13 PM
 */

package base.entity.device.model

import java.util.UUID

import base.entity.api.ApiStrings
import base.entity.api.ApiStrings.User._
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = createRequestDesc)
// todo convert to interface for mocking
case class DeviceModel(
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  uuid: UUID,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  model: Option[String] = None,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  cordova: Option[String] = None,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  platform: Option[String] = None,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  version: Option[String] = None) {
  // format: ON

}
