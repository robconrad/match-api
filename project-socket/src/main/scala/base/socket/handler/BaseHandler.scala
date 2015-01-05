/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 3:45 PM
 */

package base.socket.handler

import base.common.lib.Dispatchable
import base.common.logging.Loggable
import base.socket._
import base.socket.logging.SocketLoggable
import base.socket.message.user.UserServerCommands
import base.socket.message.{ Message, ProcessableCommand }
import io.netty.channel._
import org.json4s.JsonAST._

import scala.concurrent.Future
import scala.concurrent.duration._

abstract class BaseHandler extends ChannelInboundHandlerAdapter with SocketLoggable with Dispatchable {
  implicit val timeout: FiniteDuration = 10.seconds

  private var running = true

  protected def isProcessingMessages(implicit ctx: ChannelHandlerContext) = true

  // commands to register this handler for (receiving any other will result in disconnect)
  val commands: Map[String, ProcessableCommand[_ <: Message]]

  final def stop() {
    running = false
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
    ExceptionHandler(ctx, cause).foreach(c => warn(ctx.channel, c.toString, c))
    ctx.close
  }

  final override def channelRead(ctx: ChannelHandlerContext, msg: Object) {
    if (running && isProcessingMessages(ctx)) {
      val myMsg = msg.asInstanceOf[Option[JObject]]

      myMsg.map(_ \ "cmd") match {
        case Some(cmd: JString) if commands.isDefinedAt(cmd.values) =>
          process(ctx, cmd.values, myMsg.get)
        case Some(cmd: JString) =>
          disconnect(ctx, s"'$cmd' is not a valid command for this handler")
        case Some(cmd) =>
          disconnect(ctx, s"unable to parse cmd from message: $msg")
        case _ =>
          disconnect(ctx, "no json received in message")
      }
    }
  }

  // future off the processing of the message to keep the netty threads free of any non-connection-handling work
  private def process(ctx: ChannelHandlerContext, cmd: String, msg: JObject) {
    Future {
      commands(cmd).process(ctx, msg)
    } onFailure {
      case e => exceptionCaught(ctx, e)
    }
  }

  protected final def disconnect(ctx: ChannelHandlerContext, reason: String) {
    disconnect(ctx, Some(reason))
  }

  protected final def disconnect(ctx: ChannelHandlerContext, reason: Option[String] = None) {
    //    if (ServerService().isDebug) {
    //      if (isDebugEnabled)
    //        debug(ctx.channel, "disconnect " + ctx.channel + " for: " + reason + ": " +
    //          UserServerJson.BAD_INPUT_MSG_JSON)
    //      ctx.channel.write(UserServerJson.BAD_INPUT_MSG_JSON)
    //    } else {
    //      ctx.close
    //    }
    ctx.close()
    reason.foreach(r => warn(ctx.channel, s"disconnect: $r"))
  }

}

object BaseHandler extends Loggable {

  def init() {
    val serverCmds = UserServerCommands.cmds
    info(s"All Server Commands (direct): $serverCmds")

    // initialize handlers
    val handlerCmds = AuthenticationHandler.commands.keys
    info(s"All Server Commands (handlers): $handlerCmds")
  }

}
