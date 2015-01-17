/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 11:19 AM
 */

package base.socket.command

import base.common.service.{ Service, ServiceCompanion }
import base.entity.auth.context.AuthContext
import base.socket.command.CommandProcessingService.FutureResponse

import scala.concurrent.Future

/**
 * CRUD, etc.
 * @author rconrad
 */
trait CommandProcessingService extends Service {

  final def serviceManifest = manifest[CommandProcessingService]

  def process(input: String)(implicit authCtx: AuthContext): FutureResponse

}

object CommandProcessingService extends ServiceCompanion[CommandProcessingService] {

  type Response = Either[CommandProcessError, CommandProcessResult]
  type FutureResponse = Future[Response]

  case class CommandProcessError(reason: String)
  case class CommandProcessResult(message: Option[String], authContext: Option[AuthContext])

}
