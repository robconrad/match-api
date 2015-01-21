/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/20/15 10:34 PM
 */

package base.socket.api

import java.net.URI

import base.socket.api.impl.WebSocketApiHandlerServiceImpl
import io.netty.bootstrap.Bootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.channel.{ Channel, ChannelHandlerContext, ChannelInitializer }
import io.netty.handler.codec.http.websocketx._
import io.netty.handler.codec.http.{ DefaultHttpHeaders, HttpClientCodec, HttpObjectAggregator }
import io.netty.util.AttributeKey
import org.scalatest.concurrent.Eventually

/**
 * Responsible for testing Server startup - highest level integration test possible
 * @author rconrad
 */
// scalastyle:off
class WebSocketApiIntegrationTest extends SocketApiIntegrationTest with Eventually {

  override implicit val patienceConfig = PatienceConfig(defaultSpan)

  private val lastMessageAttr = AttributeKey.valueOf[Option[String]]("lastMessage")

  private var ch: Channel = _

  implicit class ChannelPimp(ch: Channel) {
    def lastMessage = {
      ch.attr(lastMessageAttr).get() match {
        case null => None
        case x    => x
      }
    }
    def lastMessage_=(h: Option[String]) {
      ch.attr(lastMessageAttr).set(h)
    }
  }

  def handlerService = new WebSocketApiHandlerServiceImpl

  def connect() {
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
            new HttpObjectAggregator(8192),
            handler)
        }
      })

    ch = b.connect(uri.getHost, SocketApiService().port).sync().channel()
    handler.handshakeFuture.awaitUninterruptibly()
  }

  def disconnect() {
    ch.close
  }

  def writeRead(json: String) = {
    ch.write(new TextWebSocketFrame(json))
    ch.flush()

    eventually {
      assert(ch.lastMessage.isDefined)
    }
    val response = ch.lastMessage.get

    ch.lastMessage = None
    response
  }

}
