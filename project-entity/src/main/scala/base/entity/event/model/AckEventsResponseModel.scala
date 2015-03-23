/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/22/15 6:38 PM
 */

package base.entity.event.model

import base.entity.api.ApiStrings.User._
import base.entity.event.model.impl.AckEventsResponseModelImpl
import base.entity.group.model.{ InviteModel, GroupModel }
import base.entity.json.JsonFormats
import base.entity.model.{ Model, ModelCompanion }
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = createRequestDesc)
trait AckEventsResponseModel extends Model {

  @(ApiModelProperty@field)(required = true, value = passwordDesc) def group: GroupModel

}
// format: ON

object AckEventsResponseModel extends ModelCompanion[AckEventsResponseModel, AckEventsResponseModelImpl] {

  override val formats = JsonFormats.withEnumsAndFields +
    GroupModel.serializer

}
