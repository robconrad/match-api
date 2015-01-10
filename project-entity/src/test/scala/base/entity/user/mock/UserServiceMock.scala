/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/8/15 5:22 PM
 */

package base.entity.user.mock

import base.common.service.ServiceImpl
import base.common.time.DateTimeHelper
import base.entity.auth.context.AuthContext
import base.entity.error.ApiError
import base.entity.service.CrudServiceImplHelper
import base.entity.user.UserService
import base.entity.user.model.{ LoginModel, RegisterModel, UserModel, VerifyModel }

import scala.concurrent.Future

/**
 * Fake UserService will do whatever you like
 * @author rconrad
 */
class UserServiceMock()
    extends ServiceImpl with UserService with CrudServiceImplHelper[UserModel] with DateTimeHelper {

  def register(input: RegisterModel)(implicit authCtx: AuthContext) =
    Future.successful(Left(ApiError("not implemented")))

  def verify(input: VerifyModel)(implicit authCtx: AuthContext) =
    Future.successful(Left(ApiError("not implemented")))

  def login(input: LoginModel)(implicit authCtx: AuthContext) =
    Future.successful(Left(ApiError("not implemented")))

}
