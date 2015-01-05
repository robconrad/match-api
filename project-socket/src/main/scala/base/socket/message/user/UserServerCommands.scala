/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/3/15 7:09 PM
 */

package base.socket.message.user

import base.socket.message.{ CommandObject, UserServerCommand }

object UserServerCommands extends CommandObject {

  val cmds = init(this, Login, BadApiVersion, Busy, BadInput)

  case object Login extends UserServerCommand[LoginUserServerMessage]("loginOK")
  case object BadApiVersion extends UserServerCommand[BadApiUserServerMessage]("loginOK")
  case object Busy extends UserServerCommand[BusyUserServerMessage]("serverBusy")
  case object BadInput extends UserServerCommand[BadInputUserServerMessage]("badInput")

}
