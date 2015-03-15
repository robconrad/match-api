/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/15/15 10:19 AM
 */

package base.socket.api.impl

import io.netty.channel.ChannelHandler
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.{ HttpObjectAggregator, HttpServerCodec }

/**
 * {{ Describe the high level purpose of WebSocketChannelInitializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class WebSocketChannelInitializer(commandHandler: ChannelHandler,
                                  protected val allIdleTime: Int) extends SocketChannelInitializer {

  private val maxContentLength = 65536

  override def initChannel(ch: SocketChannel) {
    val pipeline = ch.pipeline

    addIdleHandler(pipeline)

    pipeline.addLast("codec", new HttpServerCodec())
    pipeline.addLast("aggregator", new HttpObjectAggregator(maxContentLength))
    //pipeline.addLast(new WebSocketServerCompressionHandler())

    pipeline.addLast("commandHandler", commandHandler)
  }

}
