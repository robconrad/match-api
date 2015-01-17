/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 12:16 PM
 */

package base.entity.user.mock

import base.common.service.ServiceImpl
import base.entity.auth.context.AuthContext
import base.entity.command.model.CommandModel
import base.entity.error.ApiError
import base.entity.user.RegisterCommandService
import base.entity.user.RegisterCommandService.RegisterResponse
import base.entity.user.model.{ RegisterResponseModel, LoginModel, RegisterModel, VerifyModel }

import scala.concurrent.Future

/**
 * Fake UserService will do whatever you like
 * @author rconrad
 */
class RegisterCommandServiceMock(
  executeResult: RegisterResponse = Future.successful(Right(RegisterCommandService.command(RegisterResponseModel()))))
    extends ServiceImpl with RegisterCommandService {

  def execute(input: RegisterModel)(implicit authCtx: AuthContext) = executeResult

}
