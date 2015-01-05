/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/3/15 7:16 PM
 */

package base.socket.message.user

import base.socket.message.user.UserServerCommands._
import base.socket.message.{ JsonServerMessage, Message, UserServerMessage }
import org.json4s.JsonDSL._

case class BusyUserServerMessage(cmd: Busy.type = Busy) extends UserServerMessage

case class BadInputUserServerMessage(cmd: BadInput.type = BadInput) extends UserServerMessage

case class BadApiUserServerMessage(cmd: BadApiVersion.type = BadApiVersion) extends UserServerMessage

class LoginUserServerMessage(userId: Int,
                             name: String) extends UserServerMessage with JsonServerMessage {
  val cmd = Login
  protected val getJson = () =>
    ("userId" -> userId) ~
      ("name" -> name) ~
      ("cmd" -> cmd)
}

object UserServerJson {
  val BAD_API_MSG_JSON = Message.write(new BadApiUserServerMessage()) + "\r\n"
  val BUSY_MSG_JSON = Message.write(BusyUserServerMessage()) + "\r\n"
  val BAD_INPUT_MSG_JSON = Message.write(BadInputUserServerMessage()) + "\r\n"
}

