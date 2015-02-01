/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 10:22 AM
 */

package base.socket.api.impl

import base.entity.error.ApiErrorService
import base.socket.api.impl.SocketChannelInitializer.Errors
import io.netty.channel.socket.SocketChannel
import io.netty.channel.{ChannelHandlerContext, ChannelInitializer, ChannelPipeline}
import io.netty.handler.timeout.{IdleStateEvent, IdleStateHandler}
import spray.http.StatusCodes

/**
 * {{ Describe the high level purpose of SocketChannelInitializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
abstract class SocketChannelInitializer extends ChannelInitializer[SocketChannel] {

  protected def addIdleHandler(pipeline: ChannelPipeline) {
    val readWriteIdleTime = 0
    val allIdleTime = 180
    pipeline.addLast("timeout", new IdleStateHandler(readWriteIdleTime, readWriteIdleTime, allIdleTime) {
      override def channelIdle(ctx: ChannelHandlerContext, evt: IdleStateEvent) {
        ctx.channel().close(Errors.idleError)(ctx)
      }
    })
  }

}

object SocketChannelInitializer {

  object Errors {

    val idleError = ApiErrorService().statusCode("Connection has timed out.", StatusCodes.RequestTimeout)

  }

}
