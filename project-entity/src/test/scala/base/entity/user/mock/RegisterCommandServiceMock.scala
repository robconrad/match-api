/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 2:49 PM
 */

package base.entity.user.mock

import base.common.service.ServiceImpl
import base.entity.auth.context.AuthContext
import base.entity.command.model.CommandModel
import base.entity.user.RegisterCommandService
import base.entity.user.model.{ RegisterModel, RegisterResponseModel }

import scala.concurrent.Future

/**
 * Fake UserService will do whatever you like
 * @author rconrad
 */
class RegisterCommandServiceMock(
  executeResult: Future[CommandModel[_]] = Future.successful(RegisterCommandService.inCommand(RegisterResponseModel())))
    extends ServiceImpl with RegisterCommandService {

  def execute(input: RegisterModel)(implicit authCtx: AuthContext) = executeResult

}
