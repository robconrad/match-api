/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/13/15 5:19 PM
 */

package base.entity.user

import base.entity.auth.context.AuthContext
import base.entity.command.{ CommandService, CommandServiceCompanion }
import base.entity.error.ApiError
import base.entity.user.LoginCommandService.LoginResponse
import base.entity.user.model._

import scala.concurrent.Future

/**
 * User CRUD, etc.
 * @author rconrad
 */
trait LoginCommandService extends CommandService {

  final def serviceManifest = manifest[LoginCommandService]

  def login(input: LoginModel)(implicit authCtx: AuthContext): LoginResponse

}

object LoginCommandService extends CommandServiceCompanion[LoginCommandService] {

  type LoginResponse = Future[Either[ApiError, LoginResponseModel]]

}
