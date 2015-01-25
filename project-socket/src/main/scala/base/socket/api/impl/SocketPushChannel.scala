/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 12:08 PM
 */

package base.socket.api.impl

import base.entity.auth.context.PushChannel
import base.entity.command.model.CommandModel
import base.entity.json.JsonFormats
import base.socket.api.SocketApiHandlerService
import io.netty.channel.{ ChannelFuture, ChannelFutureListener, ChannelHandlerContext }
import org.json4s.native.Serialization

import scala.concurrent.{ Promise, Future }

/**
 * {{ Describe the high level purpose of SocketPushChannel here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class SocketPushChannel(implicit ctx: ChannelHandlerContext) extends PushChannel {

  private implicit val formats = JsonFormats.withModels

  def push[T <: CommandModel[_]](command: T)(implicit m: Manifest[T]) = {
    ctx.channel.isOpen match {
      case false => Future.successful(false)
      case true =>
        val p = Promise[Boolean]()
        val json = serialize(command)
        val channelFuture = SocketApiHandlerService().write(json)
        channelFuture.addListener(new ChannelFutureListener {
          def operationComplete(future: ChannelFuture) {
            p success true
          }
        })
        p.future
    }
  }

  def serialize[T <: CommandModel[_]](command: T)(implicit m: Manifest[T]) = {
    Serialization.write(command)
  }

}
