/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/21/15 9:02 PM
 */

package base.socket.api.test

import java.net.URI

import base.socket.api.SocketApiService
import io.netty.bootstrap.Bootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.channel.{ ChannelHandlerContext, ChannelInitializer }
import io.netty.handler.codec.http.websocketx.{ TextWebSocketFrame, WebSocketClientHandshakerFactory, WebSocketVersion }
import io.netty.handler.codec.http.{ DefaultHttpHeaders, HttpClientCodec, HttpObjectAggregator }

/**
 * {{ Describe the high level purpose of WebSocketClientFactory here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
// scalastyle:off null
object WebSocketClientFactory {

  val maxContentLength = 8192

  def connect() = {
    val uri = URI.create("ws://" + SocketApiService().host)
    val group = new NioEventLoopGroup()
    val handler = new WebSocketClientHandler(
      WebSocketClientHandshakerFactory.newHandshaker(uri,
        WebSocketVersion.V13, null, false, new DefaultHttpHeaders())) {
      override def channelRead0(ctx: ChannelHandlerContext, msg: AnyRef) {
        super.channelRead0(ctx, msg)
        msg match {
          case msg: TextWebSocketFrame => ctx.channel.lastMessage = Option(msg.text())
          case msg                     =>
        }
      }
    }

    val b = new Bootstrap()
    b.group(group)
      .channel(classOf[NioSocketChannel])
      .handler(new ChannelInitializer[SocketChannel]() {
        override protected def initChannel(ch: SocketChannel) {
          val p = ch.pipeline()
          p.addLast(
            new HttpClientCodec(),
            new HttpObjectAggregator(maxContentLength),
            handler)
        }
      })

    val ch = b.connect(uri.getHost, SocketApiService().port).sync().channel()
    handler.handshakeFuture.awaitUninterruptibly()
    ch
  }

}
