/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 12:59 PM
 */

package base.entity.group.model

import base.entity.api.ApiStrings.User._
import base.entity.event.model.EventModel
import base.entity.question.model.QuestionModel
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = createRequestDesc)
// todo convert to interface for mocking
case class SendInviteResponseModel(
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  group: GroupModel,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  events: List[EventModel],
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  questions: List[QuestionModel]) {
  // format: ON

}
