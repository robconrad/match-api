/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 11:45 AM
 */

package base.entity.question.model

import java.util.UUID

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
case class QuestionsResponseModel(
  @(ApiModelProperty @field)(required = true, value = emailDesc) groupId: UUID,
  @(ApiModelProperty @field)(required = true, value = emailDesc) questions: List[QuestionModel]) {
  // format: ON

}
