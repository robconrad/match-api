/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/6/15 9:01 PM
 */

package base.socket.command.user

import base.entity.error.ApiError
import base.entity.user.model.{ LoginResponseModel, RegisterResponseModel, VerifyResponseModel }
import base.socket.command.{ CommandObject, UserServerCommand }

object UserServerCommands extends CommandObject {

  val cmds = init(this, RegisterResponse, VerifyResponse, LoginResponse, Error)

  case object RegisterResponse extends UserServerCommand[RegisterResponseModel]("registerResponse")
  case object VerifyResponse extends UserServerCommand[VerifyResponseModel]("verifyResponse")
  case object LoginResponse extends UserServerCommand[LoginResponseModel]("loginResponse")
  //case object BadApiVersion extends UserServerCommand[BadApiUserServerMessage]("loginOK")
  //case object Busy extends UserServerCommand[BusyUserServerMessage]("serverBusy")
  case object Error extends UserServerCommand[ApiError]("error")

}
