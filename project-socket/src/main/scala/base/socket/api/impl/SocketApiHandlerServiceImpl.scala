/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 9:48 AM
 */

package base.socket.api.impl

import base.common.lib.Dispatchable
import base.common.service.ServiceImpl
import base.entity.api.ApiErrorCodes
import base.entity.auth.context.NoAuthContext
import base.entity.auth.context.impl.ChannelContextImpl
import base.entity.error.ApiErrorService
import base.entity.group.GroupListenerService
import base.entity.json.JsonFormats
import base.socket.api.{SocketApiHandlerService, SocketApiService, SocketApiStats, SocketApiStatsService}
import base.socket.command.CommandProcessingService
import base.socket.command.impl.CommandProcessingServiceImpl.Errors
import base.socket.logging.{LoggableChannelInfo, SocketLoggable}
import io.netty.channel.{ChannelFuture, ChannelFutureListener, ChannelHandlerContext, ChannelInboundHandlerAdapter}
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
        ctx.channel().close(ApiErrorService().throwable(Errors.externalErrorText, StatusCodes.InternalServerError, t))
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
            debug("processing succeeded with %s", result)
            result.authContext.foreach { authCtx =>
              ctx.channel.channelCtx.authCtx = authCtx
            }
            result.message.foreach { json =>
              write(json)
            }
          case Success(Left(processingError)) =>
            warn("processing failed with %s", processingError.message)
            ctx.channel().close(processingError.message)
          case Failure(t) =>
            error("processing threw", t)
            ctx.channel().close(ApiErrorService().throwable(Errors.externalErrorText, StatusCodes.InternalServerError, t))
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
      ctx.channel.close(SocketApiHandlerServiceImpl.busyJson)
    }
  }

  final def stop() {
    running = false
  }

}

object SocketApiHandlerServiceImpl {

  implicit val formats = JsonFormats.withModels

  lazy val runningText = "The server is not processing commands right now."
  lazy val runningApiError =
    ApiErrorService().errorCode(runningText, StatusCodes.ServiceUnavailable, ApiErrorCodes.SERVER_NOT_RUNNING)
  lazy val runningJson = ApiErrorService().toJson(runningApiError)

  lazy val busyText = "The server is too busy to accept new connections right now."
  lazy val busyApiError = ApiErrorService().errorCode(busyText, StatusCodes.ServiceUnavailable, ApiErrorCodes.SERVER_BUSY)
  lazy val busyJson = ApiErrorService().toJson(busyApiError)

}

