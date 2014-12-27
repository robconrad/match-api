/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.user.model

import base.entity.ApiStrings.User._
import base.entity.ApiStrings._
import base.entity.model.Email
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = updateRequestDesc)
case class PutUserRequest(
  @(ApiModelProperty @field)(required = false, value = emailDesc)                           email: Option[Email],
  @(ApiModelProperty @field)(required = false, value = passwordDesc)                        password: Option[String],
  @(ApiModelProperty @field)(required = false, value = activeDesc, dataType = boolDataType) active: Option[Boolean]
)
// format: ON
