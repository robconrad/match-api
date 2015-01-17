/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 12:29 PM
 */

package base.entity.user

import base.entity.command.model.CommandModel
import base.entity.command.{ CommandService, CommandServiceCompanion }
import base.entity.error.ApiError
import base.entity.perm.Perms
import base.entity.user.model.{ RegisterModel, RegisterResponseModel }

import scala.concurrent.Future

/**
 * User CRUD, etc.
 * @author rconrad
 */
trait RegisterCommandService extends CommandService[RegisterModel, RegisterResponseModel] {

  final def command = RegisterCommandService.cmd

  final def serviceManifest = manifest[RegisterCommandService]

  final val perms = Set(Perms.REGISTER)

}

object RegisterCommandService extends CommandServiceCompanion[RegisterCommandService] {

  type RegisterResponse = Future[Either[ApiError, CommandModel[RegisterResponseModel]]]

  final val cmd = "register"

}
