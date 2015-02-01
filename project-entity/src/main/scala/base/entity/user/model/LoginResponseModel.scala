/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 12:46 PM
 */

package base.entity.user.model

import base.entity.api.ApiStrings.User._
import base.entity.event.model.EventModel
import base.entity.group.model.GroupModel
import base.entity.question.model.QuestionModel
import com.wordnik.swagger.annotations.{ApiModel, ApiModelProperty}
import org.joda.time.DateTime

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = createRequestDesc)
// todo convert to interface for mocking
case class LoginResponseModel(
  @(ApiModelProperty @field)(required = true, value = emailDesc)     user: UserModel,
  @(ApiModelProperty @field)(required = false, value = passwordDesc) phone: Option[String],
  @(ApiModelProperty @field)(required = false, value = passwordDesc) phoneVerified: Boolean,
  @(ApiModelProperty @field)(required = false, value = passwordDesc) groups: List[GroupModel],
  @(ApiModelProperty @field)(required = false, value = passwordDesc) events: Option[List[EventModel]],
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  questions: Option[List[QuestionModel]],
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  lastLoginTime: Option[DateTime]) {
  // format: ON

}
