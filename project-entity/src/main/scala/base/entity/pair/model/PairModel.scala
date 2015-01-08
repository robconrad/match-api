/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 9:57 PM
 */

package base.entity.pair.model

import java.util.UUID

import base.entity.ApiStrings.User._
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
case class PairModel(
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  id: UUID,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  users: List[UserModel],
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  lastEventTime: DateTime,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  lastReadTime: DateTime,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  eventCount: Int) {
  // format: ON

}