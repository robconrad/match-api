/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 4:11 PM
 */

package base.socket.api.impl

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel._
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpMethod._
import io.netty.handler.codec.http.websocketx._

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
      case msg                                      => ctx.close()
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

  def makeInitializer = new WebSocketChannelInitializer(this)

}
