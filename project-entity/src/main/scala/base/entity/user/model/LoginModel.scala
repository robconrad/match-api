/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:13 PM
 */

package base.entity.user.model

import java.util.UUID

import base.entity.api.ApiStrings
import base.entity.api.ApiStrings.User._
import base.entity.api.ApiVersions.ApiVersion
import base.entity.command.model.CommandInputModel
import base.entity.device.model.DeviceModel
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = createRequestDesc)
// todo convert to interface for mocking
case class LoginModel(
  @(ApiModelProperty @field)(required = true, value = emailDesc)     fbToken: String,
  @(ApiModelProperty @field)(required = false, value = passwordDesc) groupId: Option[UUID],
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  appVersion: String,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  apiVersion: ApiVersion,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  locale: String,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  device: DeviceModel) extends CommandInputModel {
  // format: ON

  // suspicious right? It's because Login is the command that establishes groups on the auth context in the first place
  def assertGroupId = None

}
