/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 7:54 PM
 */

package base.entity.group.model

import java.util.UUID

import base.entity.api.ApiStrings.User._
import base.entity.group.model.impl.GroupModelImpl
import base.entity.model.{ Model, ModelCompanion }
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
trait GroupModel extends Model {

  @(ApiModelProperty @field)(required = true, value = passwordDesc) def id: UUID
  @(ApiModelProperty @field)(required = true, value = passwordDesc) def users: List[UserModel]
  @(ApiModelProperty @field)(required = true, value = passwordDesc) def lastEventTime: Option[DateTime]
  @(ApiModelProperty @field)(required = true, value = passwordDesc) def lastReadTime: Option[DateTime]
  @(ApiModelProperty @field)(required = true, value = passwordDesc) def eventCount: Int

}
// format: ON

object GroupModel extends ModelCompanion[GroupModel, GroupModelImpl]
