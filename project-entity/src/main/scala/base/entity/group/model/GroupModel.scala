/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/22/15 5:03 PM
 */

package base.entity.group.model

import java.util.UUID

import base.entity.api.ApiStrings.User._
import base.entity.group.model.impl.GroupModelImpl
import base.entity.json.JsonFormats
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
  @(ApiModelProperty @field)(required = true, value = passwordDesc) def users: Option[List[UserModel]]
  @(ApiModelProperty @field)(required = true, value = passwordDesc) def invites: Option[List[InviteModel]]
  @(ApiModelProperty @field)(required = true, value = passwordDesc) def lastReadEventCount: Option[Long]
  @(ApiModelProperty @field)(required = true, value = passwordDesc) def eventCount: Long

}
// format: ON

object GroupModel extends ModelCompanion[GroupModel, GroupModelImpl] {

  override val formats = JsonFormats.withEnumsAndFields +
    InviteModel.serializer

}
