/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 12:13 PM
 */

package base.entity.user.mock

import base.common.service.ServiceImpl
import base.entity.auth.context.AuthContext
import base.entity.command.model.CommandModel
import base.entity.error.ApiError
import base.entity.user.LoginCommandService
import base.entity.user.LoginCommandService.LoginResponse
import base.entity.user.model.{ LoginResponseModel, LoginModel }

import scala.concurrent.Future

/**
 * Fake UserService will do whatever you like
 * @author rconrad
 */
class LoginCommandServiceMock(
  executeResult: LoginResponse = Future.successful(Left(ApiError("not implemented"))))
    extends ServiceImpl
    with LoginCommandService {

  def execute(input: LoginModel)(implicit authCtx: AuthContext) = executeResult

}
