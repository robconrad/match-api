/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 9:48 PM
 */

package base.entity.question.model

import java.util.UUID

import base.entity.api.ApiStrings.User._
import base.entity.question.QuestionSides.QuestionSide
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = createRequestDesc)
case class AnswerModel(
  @(ApiModelProperty @field)(required = true, value = emailDesc)     questionId: UUID,
  @(ApiModelProperty @field)(required = true, value = emailDesc)     groupId: UUID,
  @(ApiModelProperty @field)(required = true, value = emailDesc)     side: QuestionSide,
  @(ApiModelProperty @field)(required = true, value = emailDesc)     response: Boolean) {
  // format: ON

}
