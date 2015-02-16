/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:17 PM
 */

package base.entity.user.model

import java.util.UUID

import base.entity.api.ApiStrings.User._
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
  @(ApiModelProperty @field)(required = true,  value = emailDesc) pictureUrl: Option[String],
  @(ApiModelProperty @field)(required = true,  value = emailDesc) name: Option[String])
// format: ON
