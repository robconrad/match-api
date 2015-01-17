/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 1:50 PM
 */

package base.socket.api.impl

import base.common.lib.Dispatchable
import base.common.service.ServiceImpl
import base.entity.api.ApiErrorCodes
import base.entity.auth.context.NoAuthContext
import base.entity.command.model.CommandModel
import base.entity.error.ApiError
import base.entity.json.JsonFormats
import base.socket.api.{ SocketApiHandlerService, SocketApiService, SocketApiStats, SocketApiStatsService }
import base.socket.command.CommandProcessingService
import base.socket.logging.{ LoggableChannelInfo, SocketLoggable }
import io.netty.buffer.{ ByteBuf, Unpooled }
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel._
import io.netty.util.CharsetUtil
import org.json4s.native.Serialization
import spray.http.StatusCodes

@Sharable
class SocketApiHandlerServiceImpl
    extends ChannelInboundHandlerAdapter
    with ServiceImpl
    with SocketApiHandlerService
    with SocketLoggable
    with Dispatchable {

  private var running = true

  private implicit def ctx2AuthCtx(implicit ctx: ChannelHandlerContext): LoggableChannelInfo = ctx.channel

  final def stop() {
    running = false
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, t: Throwable) {
    implicit val iCtx = ctx

    /**
     * Bad client = closed connection, malformed requests, etc.
     *
     * Do nothing if the exception is one of the following:
     * java.io.IOException: Connection reset by peer
     * java.io.IOException: Broken pipe
     * java.nio.channels.ClosedChannelException: null
     * javax.net.ssl.SSLException: not an SSL/TLS record (Use http://... URL to connect to HTTPS server)
     * java.lang.IllegalArgumentException: empty text (Use http://... URL to connect to HTTPS server)
     */
    val s = t.toString
    if (s.startsWith("java.nio.channels.ClosedChannelException") ||
      s.startsWith("java.io.IOException") ||
      s.startsWith("javax.net.ssl.SSLException") ||
      s.startsWith("java.lang.IllegalArgumentException")) {
      // do nothing
    } else {
      warn("command handler caught ", t)
      if (ctx.channel.isOpen) {
        ctx.channel.close()
      }
    }
  }

  override def channelRead(ctx: ChannelHandlerContext, msg: Object) {
    implicit val iCtx = ctx

    if (!running) {
      ctx.channel.write(SocketApiHandlerServiceImpl.runningJson)
    } else {
      val msgStr = msg.asInstanceOf[ByteBuf].toString(CharsetUtil.UTF_8)
      if (isDebugEnabled) debug("message received: " + msgStr)
      CommandProcessingService().process(msgStr)(ctx.channel.authCtx).map {
        case Right(result) =>
          result.authContext.foreach { authCtx =>
            ctx.channel.authCtx = authCtx
          }
          result.message.foreach { json =>
            debug("message sent: %s", json)
            val terminated = json.length > 0 && json.last == '\n' match {
              case true  => json
              case false => json + "\r\n"
            }
            val encoded = Unpooled.copiedBuffer(terminated, CharsetUtil.UTF_8)
            ctx.write(encoded)
            ctx.flush()
          }
        case Left(error) =>
          warn("processing failed with %s", error.reason)
          ctx.channel.close()
      }
    }
  }

  override def channelActive(ctx: ChannelHandlerContext) {
    implicit val iCtx = ctx

    if (SocketApiService().isConnectionAllowed) {
      SocketApiStatsService().increment(SocketApiStats.CONNECTIONS)

      ctx.channel.closeFuture().addListener(new ChannelFutureListener {
        override def operationComplete(channelFuture: ChannelFuture) {
          SocketApiStatsService().decrement(SocketApiStats.CONNECTIONS)
        }
      })
    } else {
      if (System.nanoTime() % 10 == 0) {
        warn("currentConnectionCount has exceeded maximum value")
      }
      ctx.write(SocketApiHandlerServiceImpl.busyJson)
        .addListener(ChannelFutureListener.CLOSE)
    }
  }

}

object SocketApiHandlerServiceImpl {

  implicit val formats = JsonFormats.withEnumsAndFields

  lazy val runningText = "The server is not processing commands right now."
  lazy val runningApiError = ApiError(runningText, StatusCodes.ServiceUnavailable, ApiErrorCodes.SERVER_NOT_RUNNING)
  lazy val runningJson = Serialization.write(CommandModel("error", runningApiError))

  lazy val busyText = "The server is too busy to accept new connections right now."
  lazy val busyApiError = ApiError(busyText, StatusCodes.ServiceUnavailable, ApiErrorCodes.SERVER_BUSY)
  lazy val busyJson = Serialization.write(CommandModel("error", busyApiError))

}
