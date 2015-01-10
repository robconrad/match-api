/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 3:05 PM
 */

package base.entity.user.impl

import base.common.service.ServiceImpl
import base.entity.auth.context.AuthContext
import base.entity.error.ApiError
import base.entity.kv.KvService
import base.entity.logging.AuthLoggable
import base.entity.perm.Perms
import base.entity.user.UserService
import base.entity.user.model._

import scala.concurrent.Future

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class UserServiceImpl extends ServiceImpl with UserService with AuthLoggable {

  def register(input: RegisterModel)(implicit authCtx: AuthContext) = {
    authCtx.assertHas(Perms.REGISTER)
    Future.successful(Left(ApiError("not implemented")))
  }

  def verify(input: VerifyModel)(implicit authCtx: AuthContext) = {
    authCtx.assertHas(Perms.VERIFY)
    Future.successful(Left(ApiError("not implemented")))
  }

  def login(input: LoginModel)(implicit authCtx: AuthContext) = {
    authCtx.assertHas(Perms.LOGIN)
    Future.successful(Left(ApiError("not implemented")))
  }
}
