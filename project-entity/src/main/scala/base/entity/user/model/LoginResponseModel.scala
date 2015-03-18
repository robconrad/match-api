/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/17/15 10:27 PM
 */

package base.entity.user.model

import java.util.UUID

import base.entity.api.ApiStrings.User._
import base.entity.event.model.EventModel
import base.entity.group.model.GroupModel
import base.entity.json.JsonFormats
import base.entity.model.{ Model, ModelCompanion }
import base.entity.question.model.QuestionModel
import base.entity.user.model.impl.LoginResponseModelImpl
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }
import org.joda.time.DateTime

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = createRequestDesc)
trait LoginResponseModel extends Model {

  @(ApiModelProperty @field)(required = true, value = emailDesc)     def user: UserModel
  @(ApiModelProperty @field)(required = false, value = passwordDesc) def phone: Option[String]
  @(ApiModelProperty @field)(required = false, value = passwordDesc) def phoneVerified: Boolean
  @(ApiModelProperty @field)(required = false, value = passwordDesc) def pendingGroups: List[GroupModel]
  @(ApiModelProperty @field)(required = false, value = passwordDesc) def groups: List[GroupModel]
  @(ApiModelProperty @field)(required = false, value = passwordDesc) def groupId: Option[UUID]
  @(ApiModelProperty @field)(required = false, value = passwordDesc) def events: Option[List[EventModel]]
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  def questions: Option[List[QuestionModel]]
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  def lastLoginTime: Option[DateTime]

}
// format: ON

object LoginResponseModel extends ModelCompanion[LoginResponseModel, LoginResponseModelImpl] {

  override val formats = JsonFormats.withEnumsAndFields +
    GroupModel.serializer +
    EventModel.serializer

}
