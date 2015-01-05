/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 1:20 PM
 */

package base.socket.message.user

import base.socket.message.UserClientMessage
import base.socket.message.user.UserClientCommands._

case class LoginUserClientMessage(deviceId: String,
                                  cmd: Login.type = Login) extends UserClientMessage

case class HeartbeatUserClientMessage(cmd: Heartbeat.type = Heartbeat) extends UserClientMessage

