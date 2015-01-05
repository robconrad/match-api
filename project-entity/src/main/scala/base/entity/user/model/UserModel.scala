/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 7:42 PM
 */

package base.entity.user.model

import base.common.time.DateTimeHelper
import base.entity.ApiStrings.User._
import base.entity.Tables.UserRow
import base.entity.auth.context.AuthContext
import base.entity.model.ModelImplicits
import base.entity.user.model
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }

import scala.annotation.meta.field

/**
 * API describable Response
 * @author rconrad
 */
// format: OFF
@ApiModel(description = userResponse)
case class UserModel(
  @(ApiModelProperty @field)(required = true,  value = idDesc)                            id: String,
  @(ApiModelProperty @field)(required = true,  value = emailDesc)                         email: String,
  @(ApiModelProperty @field)(required = true,  value = activeDesc)                        active: Boolean,
  @(ApiModelProperty @field)(required = true,  value = createdAtDesc)                     createdAt: String)
// format: ON

object UserModel extends ModelImplicits with DateTimeHelper {

  /**
   * Given raw db input, marshall it into an Response
   */
  def apply(user: UserRow)(implicit authCtx: AuthContext): model.UserModel =
    model.UserModel(
      user.uuid,
      user.email,
      user.active,
      user.createdAt)

}
