/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 10:56 AM
 */

package base.socket.command

import base.common.service.{Service, ServiceCompanion}
import base.entity.auth.context.{AuthContext, ChannelContext}
import base.entity.error.model.ApiError
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

  case class CommandProcessError(message: ApiError)
  case class CommandProcessResult(message: Option[String], authContext: Option[AuthContext])

}
