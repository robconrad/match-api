/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 12:19 AM
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

  implicit val formats = JsonFormats.withEnumsAndFields

  // todo test this
  def push[T <: CommandModel[_]](command: T)(implicit m: Manifest[T]) = {
    ctx.channel.isOpen match {
      case false => Future.successful(false)
      case true =>
        val p = Promise[Boolean]()
        SocketApiHandlerService().write(Serialization.write(command)).addListener(new ChannelFutureListener {
          def operationComplete(future: ChannelFuture) {
            p success true
          }
        })
        p.future
    }
  }

}
