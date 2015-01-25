/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 12:13 PM
 */

package base.socket.api.impl

import base.common.service.TestServices
import base.entity.command.model.CommandModel
import base.entity.event.model.EventModel
import base.socket.api.SocketApiHandlerService
import base.socket.test.SocketBaseSuite
import io.netty.channel.{ Channel, ChannelFuture, ChannelHandlerContext }
import io.netty.util.concurrent.GenericFutureListener

/**
 * {{ Describe the high level purpose of SocketPushChannelTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class SocketPushChannelTest extends SocketBaseSuite {

  test("push to open channel") {
    implicit val ctx = mock[ChannelHandlerContext]
    val channel = mock[Channel]
    val channelFuture = mock[ChannelFuture]
    val handler = mock[SocketApiHandlerService]
    val event = mock[EventModel]
    val command = CommandModel(event)
    val unregister = TestServices.register(handler)
    val pushChannel = new SocketPushChannel() {
      override def serialize[T <: CommandModel[_]](command: T)(implicit m: Manifest[T]) = ""
    }

    ctx.channel _ expects () returning channel
    channel.isOpen _ expects () returning true
    (handler.write(_: String)(_: ChannelHandlerContext)) expects (*, *) returning channelFuture
    channelFuture.addListener _ expects * onCall { arg: GenericFutureListener[_] =>
      arg.asInstanceOf[GenericFutureListener[ChannelFuture]].operationComplete(channelFuture)
      channelFuture
    }

    assert(pushChannel.push(command).await())
    unregister()
  }

  test("push to closed channel") {
    implicit val ctx = mock[ChannelHandlerContext]
    val channel = mock[Channel]
    val event = mock[EventModel]
    val command = CommandModel(event)
    val pushChannel = new SocketPushChannel()

    ctx.channel _ expects () returning channel
    channel.isOpen _ expects () returning false

    assert(!pushChannel.push(command).await())
  }

}
