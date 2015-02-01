/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 8:06 PM
 */

package base.socket.api.impl

import io.netty.channel.ChannelHandler
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.{DelimiterBasedFrameDecoder, Delimiters}

/**
 * {{ Describe the high level purpose of RawSocketChannelInitializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class RawSocketChannelInitializer(commandHandler: ChannelHandler) extends SocketChannelInitializer {

  val maxFrameLength = 8192

  override def initChannel(ch: SocketChannel) {
    val pipeline = ch.pipeline
    pipeline.addLast("framer", new DelimiterBasedFrameDecoder(maxFrameLength, Delimiters.lineDelimiter: _*))

    addIdleHandler(pipeline)

    pipeline.addLast("commandHandler", commandHandler)
  }

}
