/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:13 PM
 */

package base.entity.user.model

import base.entity.api.ApiStrings
import base.entity.api.ApiStrings.User._
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = createRequestDesc)
// todo convert to interface for mocking
case class RegisterPhoneResponseModel(
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  phone: String) {
  // format: ON

}

