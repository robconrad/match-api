/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 2:42 PM
 */

package base.entity.user.model

import base.entity.api.ApiStrings
import ApiStrings.User._
import com.wordnik.swagger.annotations.{ ApiModelProperty, ApiModel }

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = createRequestDesc)
case class RegisterResponseModel() {
  // format: ON

}

