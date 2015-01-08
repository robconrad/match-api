/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/6/15 9:35 PM
 */

package base.entity.user.model

import java.util.UUID

import base.entity.ApiStrings.User._
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = createRequestDesc)
case class VerifyResponseModel(
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  token: UUID) {
  // format: ON

}

