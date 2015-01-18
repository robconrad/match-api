/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 10:39 PM
 */

package base.entity.message.model

import java.util.UUID

import base.entity.api.ApiStrings.User._
import com.wordnik.swagger.annotations.{ ApiModelProperty, ApiModel }

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = createRequestDesc)
case class MessageModel(
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  groupId: UUID,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  body: String) {
  // format: ON

}