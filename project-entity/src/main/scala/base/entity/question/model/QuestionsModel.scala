/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 7:12 PM
 */

package base.entity.question.model

import java.util.UUID

import base.entity.api.ApiStrings.User._
import base.entity.command.model.CommandInputModel
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = createRequestDesc)
// todo convert to interface for mocking
case class QuestionsModel(
  @(ApiModelProperty @field)(required = true, value = emailDesc) groupId: UUID) extends CommandInputModel {
  // format: ON

  lazy val assertGroupId = Option(groupId)

}
