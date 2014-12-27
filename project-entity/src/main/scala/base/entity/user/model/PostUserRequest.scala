/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 5:10 PM
 */

package base.entity.user.model

import base.common.time.DateTimeHelper
import base.entity.ApiStrings.User._
import base.entity.Tables.UserRowBuilder
import base.entity.auth.context.AuthContext
import base.entity.auth.impl.AuthServiceImpl
import base.entity.error.ApiError
import base.entity.model.Email
import com.wordnik.swagger.annotations.{ ApiModel, ApiModelProperty }
import org.joda.time.DateTime

import scala.annotation.meta.field


/**
 * API describable model
 * @author rconrad
 */
// format: OFF
@ApiModel(description = createRequestDesc)
case class PostUserRequest(
  @(ApiModelProperty @field)(required = true, value = emailDesc)     email: Email,
  @(ApiModelProperty @field)(required = true, value = passwordDesc)  password: String) extends DateTimeHelper {
  // format: ON

  /**
   * Validate all fields that have validation functions
   */
  def validate(implicit authCtx: AuthContext): Option[ApiError] = None

  def toRow(createdAt: DateTime = now)(implicit authCtx: AuthContext) = {
    val salt = AuthServiceImpl.getPasswordSalt
    val hash = AuthServiceImpl.getPasswordHash(password, salt)

    UserRowBuilder(
      email = email,
      passwordHash = hash,
      passwordSalt = salt,
      resetCode = None,
      resetExpiresAt = None,
      createdAt = createdAt,
      active = true)
  }

}
