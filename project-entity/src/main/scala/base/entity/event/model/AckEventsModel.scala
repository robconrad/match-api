/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/22/15 5:03 PM
 */

package base.entity.event.model

import java.util.UUID

import base.entity.api.ApiStrings.User._
import base.entity.command.model.CommandInputModel
import base.entity.event.model.impl.AckEventsModelImpl
import base.entity.model.ModelCompanion
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = createRequestDesc)
trait AckEventsModel extends CommandInputModel {

  @(ApiModelProperty @field)(required = true, value = passwordDesc) def groupId: UUID
  @(ApiModelProperty @field)(required = true, value = passwordDesc) def lastReadEventCount: Long

}
// format: ON

object AckEventsModel extends ModelCompanion[AckEventsModel, AckEventsModelImpl]
