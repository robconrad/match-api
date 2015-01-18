/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 5:43 PM
 */

package base.entity.group.model

import java.util.UUID

import base.entity.api.ApiStrings
import ApiStrings.User._
import base.entity.user.model.UserModel
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }
import org.joda.time.DateTime

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = createRequestDesc)
case class GroupModel(
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  id: UUID,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  users: Iterable[UserModel],
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  lastEventTime: Option[DateTime],
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  lastReadTime: Option[DateTime],
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  eventCount: Int) {
  // format: ON

}

case class GroupModelBuilder(id: Option[UUID] = None,
                             users: Option[Iterable[UserModel]] = None,
                             lastEventTime: Option[DateTime] = None,
                             lastReadTime: Option[DateTime] = None,
                             eventCount: Option[Int] = None) {

  def build = GroupModel(
    id = id.get,
    users = users.get,
    lastEventTime = lastEventTime,
    lastReadTime = lastReadTime,
    eventCount = eventCount.get)

}
