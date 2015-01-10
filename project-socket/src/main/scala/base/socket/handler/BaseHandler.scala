/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/9/15 10:03 PM
 */

package base.socket.handler

import base.common.lib.Dispatchable
import base.common.logging.Loggable
import base.entity.auth.context.NoAuthContext
import base.socket._
import base.socket.command.ProcessableCommand
import base.socket.command.user.UserServerCommands
import base.socket.logging.SocketLoggable
import io.netty.channel._
import org.json4s.JsonAST._

import scala.concurrent.Future
import scala.concurrent.duration._

// scalastyle:off null
abstract class BaseHandler extends ChannelInboundHandlerAdapter with SocketLoggable with Dispatchable {
  implicit val timeout: FiniteDuration = 10.seconds

  private var running = true

  protected def isProcessingMessages(implicit ctx: ChannelHandlerContext) = true

  // commands to register this handler for (receiving any other will result in disconnect)
  val commands: Map[String, ProcessableCommand[_]]

  final def stop() {
    running = false
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
    ExceptionHandler(ctx, cause).foreach(c => warn(c.toString, c)(ctx))
    ctx.close
  }

  // expecting { "cmd": "$name", "body": { $body } }
  final override def channelRead(ctx: ChannelHandlerContext, msg: Object) {
    if (running && isProcessingMessages(ctx)) {
      msg match {
        case Some(msg: JObject) =>
          msg \ "cmd" match {
            case cmd: JString if commands.isDefinedAt(cmd.values) =>
              msg \ "body" match {
                case body: JObject => process(ctx, cmd.values, body)
                case _             => disconnect(ctx, s"unable to parse body from message: $msg")
              }
            case cmd: JString => disconnect(ctx, s"'$cmd' is not a valid command for this handler")
            case _            => disconnect(ctx, s"unable to parse cmd from message: $msg")
          }
        case Some(msg) => disconnect(ctx, s"unable to parse object from message: $msg")
        case _         => disconnect(ctx, "no json received in message")
      }
    }
  }

  // future off the processing of the message to keep the netty threads free of any non-connection-handling work
  private def process(ctx: ChannelHandlerContext, cmd: String, msg: JObject) {
    Future {
      if (ctx.channel.authCtx == None) {
        ctx.channel.authCtx = NoAuthContext
      }
      commands(cmd).process(ctx, msg)
    } onFailure {
      case e => exceptionCaught(ctx, e)
    }
  }

  protected final def disconnect(ctx: ChannelHandlerContext, reason: String) {
    disconnect(ctx, Some(reason))
  }

  protected final def disconnect(ctx: ChannelHandlerContext, reason: Option[String] = None) {
    ctx.close()
    reason.foreach(r => warn(s"disconnect: $r")(ctx))
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
