/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/15/15 10:19 AM
 */

package base.socket.api.impl

import base.entity.error.ApiErrorService
import base.socket.api.impl.WebSocketApiHandlerServiceImpl.Errors
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel._
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpMethod._
import io.netty.handler.codec.http.websocketx._
import spray.http.StatusCodes

import scala.concurrent.duration.FiniteDuration

@Sharable // scalastyle:off
class WebSocketApiHandlerServiceImpl extends SocketApiHandlerServiceImpl {

  def write(json: String)(implicit ctx: ChannelHandlerContext) = {
    debug("message sent: %s", json)
    ctx.channel.writeAndFlush(new TextWebSocketFrame(json))
  }

  override def channelRead(ctx: ChannelHandlerContext, msg: Object) {
    implicit val iCtx = ctx
    msg match {
      case msg: FullHttpRequest if isHttpValid(msg) => sendHandshake(msg)
      case msg: CloseWebSocketFrame                 => closeHandshake(msg)
      case msg: TextWebSocketFrame                  => read(msg.text())
      case msg                                      => ctx.channel.close(Errors.unexpectedContentError)
    }
  }

  private def isHttpValid(msg: FullHttpRequest) = {
    msg.getDecoderResult.isSuccess && msg.getMethod == GET
  }

  private def sendHandshake(req: FullHttpRequest)(implicit ctx: ChannelHandlerContext) {
    val wsFactory = new WebSocketServerHandshakerFactory("", null, true)
    ctx.channel.handshaker = wsFactory.newHandshaker(req)
    if (ctx.channel.handshaker == null) {
      WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel())
    } else {
      ctx.channel.handshaker.handshake(ctx.channel, req)
    }
  }

  private def closeHandshake(frame: CloseWebSocketFrame)(implicit ctx: ChannelHandlerContext) {
    ctx.channel.handshaker.close(ctx.channel, frame.retain)
  }

  def makeInitializer(idleTimeout: FiniteDuration) =
    new WebSocketChannelInitializer(this, idleTimeout.toSeconds.toInt)

}

object WebSocketApiHandlerServiceImpl {

  object Errors {

    val unexpectedContentError = ApiErrorService().statusCode("Unexpected message content.", StatusCodes.BadRequest)

  }

}
