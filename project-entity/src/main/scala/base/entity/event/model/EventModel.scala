/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/12/15 8:45 PM
 */

package base.entity.event.model

import java.util.UUID

import base.entity.api.ApiStrings.User._
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }
import org.joda.time.DateTime

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = createRequestDesc)
case class EventModel(
  @(ApiModelProperty @field)(required = true, value = passwordDesc) id: UUID,
  @(ApiModelProperty @field)(required = true, value = passwordDesc) userId: Option[UUID],
  @(ApiModelProperty @field)(required = true, value = passwordDesc) `type`: DateTime,
  @(ApiModelProperty @field)(required = true, value = passwordDesc) body: String,
  @(ApiModelProperty @field)(required = true, value = passwordDesc) time: DateTime) {
  // format: ON

}
