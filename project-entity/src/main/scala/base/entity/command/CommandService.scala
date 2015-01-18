/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 2:46 PM
 */

package base.entity.command

import base.common.service.Service
import base.entity.auth.context.AuthContext
import base.entity.command.model.CommandModel
import base.entity.error.ApiError
import base.entity.perm.Perms.Perm

import scala.concurrent.Future

/**
 * A Command is a combination of an HTTP Verb and an endpoint - PostFoo, GetBar, PatchBaz, etc.
 * @author rconrad
 */
trait CommandService[A, B] extends Service {

  def inCmd: String
  def outCmd: String
  final def errorCmd = CommandService.errorCmd

  def perms: Iterable[Perm]

  def execute(input: A)(implicit authCtx: AuthContext): Future[CommandModel[_]]

}

object CommandService {

  val errorCmd = "error"

  def errorCommand(response: ApiError) = CommandModel(errorCmd, response)

}
