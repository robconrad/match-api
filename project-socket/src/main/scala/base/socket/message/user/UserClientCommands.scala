/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 2:41 PM
 */

package base.socket.message.user

import base.socket.handler.AuthenticationHandler
import base.socket.message.{ CommandObject, UserClientCommand }
import io.netty.channel.ChannelHandlerContext

object UserClientCommands extends CommandObject {

  val cmds = init(this, Login, Heartbeat)

  case object Login extends UserClientCommand[LoginUserClientMessage]("login") {
    protected def process(implicit ctx: ChannelHandlerContext, msg: LoginUserClientMessage) {
      AuthenticationHandler.handleUserLogin()
    }
  }
  case object Heartbeat extends UserClientCommand[HeartbeatUserClientMessage]("heartbeat") {
    protected def process(implicit ctx: ChannelHandlerContext, msg: HeartbeatUserClientMessage) {
      // do nothing, heartbeat just keeps the connection alive
    }
  }

}
