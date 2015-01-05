/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 10:26 PM
 */

package base.socket.command.user

import base.entity.user.UserService
import base.entity.user.model.{ LoginModel, RegisterModel, VerifyModel }
import base.socket.command.{ CommandObject, UserClientCommand }
import base.socket.model.HeartbeatModel
import io.netty.channel.ChannelHandlerContext

object UserClientCommands extends CommandObject {

  val cmds = init(this, Register, Verify, Login, Heartbeat)

  case object Register extends UserClientCommand[RegisterModel]("register") {
    protected def process(implicit ctx: ChannelHandlerContext, msg: RegisterModel) {
      //AuthenticationHandler.handleUserLogin()
      UserService().register(msg).foreach(ctx.channel().write)
    }
  }

  case object Verify extends UserClientCommand[VerifyModel]("verify") {
    protected def process(implicit ctx: ChannelHandlerContext, msg: VerifyModel) {
      //AuthenticationHandler.handleUserLogin()
      UserService().verify(msg).foreach(ctx.channel().write)
    }
  }

  case object Login extends UserClientCommand[LoginModel]("login") {
    protected def process(implicit ctx: ChannelHandlerContext, msg: LoginModel) {
      //AuthenticationHandler.handleUserLogin()
      UserService().login(msg).foreach(ctx.channel().write)
    }
  }

  case object Heartbeat extends UserClientCommand[HeartbeatModel]("heartbeat") {
    protected def process(implicit ctx: ChannelHandlerContext, msg: HeartbeatModel) {
      // do nothing, heartbeat just keeps the connection alive
    }
  }

}
