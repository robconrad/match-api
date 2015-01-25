/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 12:19 AM
 */

package base.socket.api.impl

import base.common.lib.Dispatchable
import base.common.service.ServiceImpl
import base.entity.api.ApiErrorCodes
import base.entity.auth.context.NoAuthContext
import base.entity.auth.context.impl.ChannelContextImpl
import base.entity.command.CommandService
import base.entity.error.ApiError
import base.entity.group.GroupListenerService
import base.entity.json.JsonFormats
import base.socket.api.{SocketApiHandlerService, SocketApiService, SocketApiStats, SocketApiStatsService}
import base.socket.command.CommandProcessingService
import base.socket.logging.{LoggableChannelInfo, SocketLoggable}
import io.netty.channel.{ChannelFuture, ChannelFutureListener, ChannelHandlerContext, ChannelInboundHandlerAdapter}
import org.json4s.native.Serialization
import spray.http.StatusCodes

import scala.util.{Failure, Success}

/**
 * {{ Describe the high level purpose of SocketApiHandlerServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
abstract class SocketApiHandlerServiceImpl
    extends ChannelInboundHandlerAdapter
    with ServiceImpl
    with SocketApiHandlerService
    with SocketLoggable
    with Dispatchable {

  private var running = true

  protected implicit def ctx2AuthCtx(implicit ctx: ChannelHandlerContext): LoggableChannelInfo = ctx.channel

  final override def exceptionCaught(ctx: ChannelHandlerContext, t: Throwable) {
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

  def read(msg: String)(implicit ctx: ChannelHandlerContext) {
    running match {
      case false => write(SocketApiHandlerServiceImpl.runningJson)
      case true =>
        if (isDebugEnabled) debug("message received: " + msg)
        CommandProcessingService().process(msg)(ctx.channel.channelCtx).onComplete {
          case Success(Right(result)) =>
            result.authContext.foreach { authCtx =>
              ctx.channel.channelCtx.authCtx = authCtx
            }
            result.message.foreach { json =>
              write(json)
            }
          case Success(Left(error)) =>
            warn("processing failed with %s", error.reason)
            ctx.channel.close()
          case Failure(t) =>
            error("processing threw", t)
            ctx.channel.close()
        }
    }
  }

  // todo refactor this to make it testable
  final override def channelActive(ctx: ChannelHandlerContext) {
    implicit val iCtx = ctx
    ctx.channel.channelCtx = ChannelContextImpl(NoAuthContext, new SocketPushChannel())

    if (SocketApiService().isConnectionAllowed) {
      SocketApiStatsService().increment(SocketApiStats.CONNECTIONS)

      // todo test this
      ctx.channel.closeFuture().addListener(new ChannelFutureListener {
        override def operationComplete(channelFuture: ChannelFuture) {
          SocketApiStatsService().decrement(SocketApiStats.CONNECTIONS)
          ctx.channel.authCtx.user map { user =>
            GroupListenerService().unregister(user.id)(ctx.channel.channelCtx)
          }
        }
      })
    } else {
      if (System.nanoTime() % 10 == 0) {
        warn("currentConnectionCount has exceeded maximum value")
      }
      write(SocketApiHandlerServiceImpl.busyJson)
        .addListener(ChannelFutureListener.CLOSE)
    }
  }

  final def stop() {
    running = false
  }

}

object SocketApiHandlerServiceImpl {

  implicit val formats = JsonFormats.withEnumsAndFields

  lazy val runningText = "The server is not processing commands right now."
  lazy val runningApiError = ApiError(runningText, StatusCodes.ServiceUnavailable, ApiErrorCodes.SERVER_NOT_RUNNING)
  lazy val runningJson = Serialization.write(CommandService.errorCommand(runningApiError))

  lazy val busyText = "The server is too busy to accept new connections right now."
  lazy val busyApiError = ApiError(busyText, StatusCodes.ServiceUnavailable, ApiErrorCodes.SERVER_BUSY)
  lazy val busyJson = Serialization.write(CommandService.errorCommand(busyApiError))

}

