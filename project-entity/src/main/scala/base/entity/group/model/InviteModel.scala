/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 1:02 PM
 */

package base.entity.group.model

import base.entity.api.ApiStrings.User._
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = createRequestDesc)
case class InviteModel(
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  phone: String,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  label: String) {
  // format: ON

}
