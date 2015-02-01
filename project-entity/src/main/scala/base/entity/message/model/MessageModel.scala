/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 10:08 AM
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
// todo convert to interface for mocking
case class MessageModel(
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  groupId: UUID,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  text: String) {
  // format: ON

}
