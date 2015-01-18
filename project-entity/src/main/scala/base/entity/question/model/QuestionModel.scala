/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 11:39 AM
 */

package base.entity.question.model

import java.util.UUID

import base.entity.api.ApiStrings.User._
import base.entity.question.QuestionDef
import base.entity.question.QuestionSides.QuestionSide
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = createRequestDesc)
case class QuestionModel(
  @(ApiModelProperty @field)(required = true, value = emailDesc)     id: UUID,
  @(ApiModelProperty @field)(required = true, value = emailDesc)     a: String,
  @(ApiModelProperty @field)(required = true, value = emailDesc)     b: Option[String],
  @(ApiModelProperty @field)(required = true, value = emailDesc)     side: QuestionSide) {
  // format: ON

}

object QuestionModel {

  def apply(`def`: QuestionDef, side: QuestionSide): QuestionModel =
    apply(`def`.id, `def`.a, `def`.b, side)

}
