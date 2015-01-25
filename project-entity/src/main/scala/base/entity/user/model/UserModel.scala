/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 11:45 AM
 */

package base.entity.user.model

import java.util.UUID

import base.entity.api.ApiStrings
import ApiStrings.User._
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }

import scala.annotation.meta.field

/**
 * API describable Response
 * @author rconrad
 */
// format: OFF
@ApiModel(description = userResponse)
// todo convert to interface for mocking
case class UserModel(
  @(ApiModelProperty @field)(required = true,  value = idDesc)    id: UUID,
  @(ApiModelProperty @field)(required = true,  value = emailDesc) label: Option[String])
// format: ON
