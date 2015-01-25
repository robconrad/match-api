/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 9:39 PM
 */

package base.socket.command

import base.common.service.{ Service, ServiceCompanion }
import base.entity.auth.context.{ ChannelContext, AuthContext }
import base.socket.command.CommandProcessingService.FutureResponse

import scala.concurrent.Future

/**
 * CRUD, etc.
 * @author rconrad
 */
trait CommandProcessingService extends Service {

  final val serviceManifest = manifest[CommandProcessingService]

  def process(input: String)(implicit channelCtx: ChannelContext): FutureResponse

}

object CommandProcessingService extends ServiceCompanion[CommandProcessingService] {

  type Response = Either[CommandProcessError, CommandProcessResult]
  type FutureResponse = Future[Response]

  case class CommandProcessError(reason: String)
  case class CommandProcessResult(message: Option[String], authContext: Option[AuthContext])

}
