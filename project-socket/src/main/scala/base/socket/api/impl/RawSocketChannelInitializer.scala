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
import io.netty.handler.codec.{ DelimiterBasedFrameDecoder, Delimiters }
import io.netty.handler.timeout.{ IdleStateEvent, IdleStateHandler }

/**
 * {{ Describe the high level purpose of RawSocketChannelInitializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class RawSocketChannelInitializer(commandHandler: ChannelHandler) extends ChannelInitializer[SocketChannel] {

  override def initChannel(ch: SocketChannel) {
    val pipeline = ch.pipeline
    val maxFrameLength = 8192
    pipeline.addLast("framer", new DelimiterBasedFrameDecoder(maxFrameLength, Delimiters.lineDelimiter: _*))

    val readWriteIdleTime = 0
    val allIdleTime = 60
    pipeline.addLast("timeout", new IdleStateHandler(readWriteIdleTime, readWriteIdleTime, allIdleTime) {
      override def channelIdle(ctx: ChannelHandlerContext, evt: IdleStateEvent) {
        ctx.close()
      }
    })

    pipeline.addLast("commandHandler", commandHandler)
  }

}
