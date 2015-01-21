/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/20/15 10:34 PM
 */

package base.socket.api.impl

import base.socket.api.SocketApiHandlerService
import io.netty.channel.socket.SocketChannel
import io.netty.channel.{ ChannelHandler, ChannelHandlerContext, ChannelInitializer }
import io.netty.handler.codec.http.{ HttpObjectAggregator, HttpServerCodec }
import io.netty.handler.timeout.{ IdleStateEvent, IdleStateHandler }

/**
 * {{ Describe the high level purpose of WebSocketChannelInitializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class WebSocketChannelInitializer(commandHandler: ChannelHandler) extends ChannelInitializer[SocketChannel] {

  private val maxContentLength = 65536
  private val readWriteIdleTime = 0
  private val allIdleTime = 60

  override def initChannel(ch: SocketChannel) {
    val pipeline = ch.pipeline

    pipeline.addLast("timeout", new IdleStateHandler(readWriteIdleTime, readWriteIdleTime, allIdleTime) {
      override def channelIdle(ctx: ChannelHandlerContext, evt: IdleStateEvent) {
        ctx.close()
      }
    })

    pipeline.addLast("codec", new HttpServerCodec())
    pipeline.addLast("aggregator", new HttpObjectAggregator(maxContentLength))
    //pipeline.addLast(new WebSocketServerCompressionHandler())

    pipeline.addLast("commandHandler", commandHandler)
  }

}
