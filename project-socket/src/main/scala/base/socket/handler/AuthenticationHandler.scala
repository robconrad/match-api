/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 10:19 PM
 */

package base.socket.handler

import base.entity.user.model.LoginModel
import base.socket._
import base.socket.command.Command
import base.socket.command.user.UserClientCommands
import io.netty.channel._
import io.netty.channel.group.DefaultChannelGroup
import io.netty.util.concurrent.GlobalEventExecutor

/**
 * Handle incoming messages from clients
 */
@ChannelHandler.Sharable
object AuthenticationHandler extends BaseHandler {

  val allChannels = new DefaultChannelGroup("allChannels", GlobalEventExecutor.INSTANCE)

  // commands to register this handler for (receiving any other will result in disconnect)
  val commands = Command.map(
    // login commands, will remove AuthenticationHandler from pipeline and add appropriate new handler
    UserClientCommands.Register,
    UserClientCommands.Verify,
    UserClientCommands.Login,
    UserClientCommands.Heartbeat)

  override def channelActive(ctx: ChannelHandlerContext) {
    // kick off any connection creation side effects
    channelCreateSideEffects(ctx)

    // add a disconnect listener to handle any destroy side effects asynchronously
    ctx.channel.closeFuture().addListener(new ChannelFutureListener {
      override def operationComplete(channelFuture: ChannelFuture) {
        channelDestroySideEffects(channelFuture)
      }
    })
  }

  protected override def isProcessingMessages(implicit ctx: ChannelHandlerContext) = {
    //    if (ServerStats.CONNECTIONS.get() > ServerService().getConnectionCountMax) {
    //      // cut logging to 10% since we are already in a high volume situation
    //      if (System.nanoTime() % 10 == 0) {
    //        warn(ctx.channel, "currentConnectionCount has exceeded maximum value")
    //      }
    //      ctx.channel.write(UserServerJson.BUSY_MSG_JSON).addListener(ChannelFutureListener.CLOSE)
    //      false
    //    }
    true
  }

  private def channelCreateSideEffects(ctx: ChannelHandlerContext) {
    //    ServerStats.CONNECTIONS.incr()
    allChannels.add(ctx.channel)
  }

  private def channelDestroySideEffects(channelFuture: ChannelFuture) {
    //    channelFuture.channel.userSafe match {
    //      // user exists on session, have them leave whatever room they are in (if they are in one)
    //      case Some(user) => user.leaveRoom(channelFuture.channel).foreach(v => ServerStats.CONNECTIONS.decr())
    //      // no user on session, no other side effects
    //      case _          => ServerStats.CONNECTIONS.decr()
    //    }
  }

  // This method is used to send the request asynchronously upstream
  private def writeToChannelHandlerContext(handler: BaseHandler, ctx: ChannelHandlerContext, result: Boolean) {
    if (isDebugEnabled) debug(ctx.channel, s"writeToChannelHandlerContext result: $result")

    def removeAndFire() {
      if (isDebugEnabled) debug(ctx.channel, "removeAndFire " + ctx.channel + " result: " + result)
      if (!result) {
        disconnect(ctx, "Unable to login")
      } else {
        if (ctx.channel().isOpen) {
          ctx.pipeline.addLast(handler.getClass.getSimpleName, handler)
          ctx.pipeline().remove(this)
        }
      }
    }

    val eventLoop = ctx.channel().eventLoop()
    eventLoop.inEventLoop match {
      case true => removeAndFire()
      case false =>
        eventLoop.execute(new Runnable() {
          override def run() {
            removeAndFire()
          }
        })
    }
  }

  def handleUserLogin()(implicit ctx: ChannelHandlerContext, input: LoginModel) {
    writeToChannelHandlerContext(AuthenticationHandler, ctx, result = true)
  }

}
