/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 10:44 PM
 */

package base.entity.user.model

import java.util.UUID

import base.entity.ApiStrings.User._
import base.entity.api.ApiVersions.ApiVersion
import base.entity.device.model.DeviceModel
import base.entity.pair.model.PairModel
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = createRequestDesc)
case class LoginResponseModel(
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  cmd: String = "loginResponse",
  @(ApiModelProperty @field)(required = true, value = emailDesc)     userId: UUID,
  @(ApiModelProperty @field)(required = false, value = passwordDesc) pairs: List[PairModel],
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  appVersion: String,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  apiVersion: ApiVersion,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  locale: String,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  device: DeviceModel) {
  // format: ON

}
