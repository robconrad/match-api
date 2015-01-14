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
import base.entity.user.RegisterCommandService.RegisterResponse
import base.entity.user.model._

import scala.concurrent.Future

/**
 * User CRUD, etc.
 * @author rconrad
 */
trait RegisterCommandService extends CommandService {

  final def serviceManifest = manifest[RegisterCommandService]

  def register(input: RegisterModel)(implicit authCtx: AuthContext): RegisterResponse

}

object RegisterCommandService extends CommandServiceCompanion[RegisterCommandService] {

  type RegisterResponse = Future[Either[ApiError, RegisterResponseModel]]

}
