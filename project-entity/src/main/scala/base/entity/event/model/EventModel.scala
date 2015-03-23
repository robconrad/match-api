/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/22/15 5:43 PM
 */

package base.entity.event.model

import java.util.UUID

import base.entity.api.ApiStrings.User._
import base.entity.event.EventTypes.EventType
import base.entity.event.model.impl.EventModelImpl
import base.entity.group.model.GroupModel
import base.entity.model.{ Model, ModelCompanion }
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }
import org.joda.time.DateTime

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = createRequestDesc)
trait EventModel extends Model {

  @(ApiModelProperty @field)(required = true, value = passwordDesc) def id: UUID
  @(ApiModelProperty @field)(required = true, value = passwordDesc) def group: Option[GroupModel]
  @(ApiModelProperty @field)(required = true, value = passwordDesc) def groupId: UUID
  @(ApiModelProperty @field)(required = true, value = passwordDesc) def userId: Option[UUID]
  @(ApiModelProperty @field)(required = true, value = passwordDesc) def `type`: EventType
  @(ApiModelProperty @field)(required = true, value = passwordDesc) def body: String
  @(ApiModelProperty @field)(required = true, value = passwordDesc) def time: DateTime

}
// format: ON

object EventModel extends ModelCompanion[EventModel, EventModelImpl]
