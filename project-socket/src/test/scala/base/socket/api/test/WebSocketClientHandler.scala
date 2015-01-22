/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/21/15 8:55 PM
 */

package base.socket.api.test

import io.netty.channel._
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.websocketx._
import io.netty.util.CharsetUtil

/**
 * {{ Describe the high level purpose of WebSocketClientHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
// scalastyle:off
class WebSocketClientHandler(handshaker: WebSocketClientHandshaker) extends SimpleChannelInboundHandler[AnyRef] {

  var handshakeFuture: ChannelPromise = null

  override def handlerAdded(ctx: ChannelHandlerContext) {
    handshakeFuture = ctx.newPromise
  }

  override def channelActive(ctx: ChannelHandlerContext) {
    handshaker.handshake(ctx.channel)
  }

  override def channelInactive(ctx: ChannelHandlerContext) {
    System.out.println("WebSocket Client disconnected!")
  }

  def channelRead0(ctx: ChannelHandlerContext, msg: AnyRef) {
    val ch: Channel = ctx.channel
    if (!handshaker.isHandshakeComplete) {
      handshaker.finishHandshake(ch, msg.asInstanceOf[FullHttpResponse])
      System.out.println("WebSocket Client connected!")
      handshakeFuture.setSuccess()
      return
    }
    if (msg.isInstanceOf[FullHttpResponse]) {
      val response: FullHttpResponse = msg.asInstanceOf[FullHttpResponse]
      throw new IllegalStateException("Unexpected FullHttpResponse (getStatus=" +
        response.getStatus + ", content=" + response.content.toString(CharsetUtil.UTF_8) + ')')
    }
    val frame: WebSocketFrame = msg.asInstanceOf[WebSocketFrame]
    if (frame.isInstanceOf[TextWebSocketFrame]) {
      val textFrame: TextWebSocketFrame = frame.asInstanceOf[TextWebSocketFrame]
      System.out.println("WebSocket Client received message: " + textFrame.text)
    } else if (frame.isInstanceOf[PongWebSocketFrame]) {
      System.out.println("WebSocket Client received pong")
    } else if (frame.isInstanceOf[CloseWebSocketFrame]) {
      System.out.println("WebSocket Client received closing")
      ch.close
    }
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
    cause.printStackTrace
    if (!handshakeFuture.isDone) {
      handshakeFuture.setFailure(cause)
    }
    ctx.close
  }

}
