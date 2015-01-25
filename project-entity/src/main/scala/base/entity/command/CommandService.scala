/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/24/15 11:40 PM
 */

package base.entity.command

import base.common.service.Service
import base.entity.auth.context.ChannelContext
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
  def outCmd: Option[String]
  final val errorCmd = CommandService.errorCmd

  def perms: Iterable[Perm]

  def execute(input: A)(implicit channelCtx: ChannelContext): Future[Option[CommandModel[_]]]

}

object CommandService {

  val errorCmd = "error"

  def errorCommand(response: ApiError) = CommandModel(errorCmd, response)

}
