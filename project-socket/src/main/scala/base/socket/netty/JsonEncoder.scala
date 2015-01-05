/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 4:37 PM
 */

package base.socket.netty

import base.common.lib.Tryo
import base.socket._
import base.socket.logging.SocketLoggable
import base.socket.message.{ CommandMessage, Message }
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ ChannelHandlerContext, ChannelOutboundHandlerAdapter, ChannelPromise }
import io.netty.util.CharsetUtil

/**
 * {{ Describe the high level purpose of JsonEncoder here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
@Sharable
object JsonEncoder extends ChannelOutboundHandlerAdapter with SocketLoggable {
  override def write(ctx: ChannelHandlerContext, msg: Object, promise: ChannelPromise) {
    val json = msg match {
      case msg: CommandMessage => Tryo(Message.write(msg))
      case _                   => Some(msg.toString)
    }
    json match {
      case Some(json: String) =>
        if (isDebugEnabled) debug(ctx.channel, "message sent: " + json)
        val terminated = json.length > 0 && json.last == '\n' match {
          case true  => json
          case false => json + "\r\n"
        }
        val encoded = Unpooled.copiedBuffer(terminated, CharsetUtil.UTF_8)
        ctx.write(encoded)
        ctx.flush()

      case _ => error(s"failed to encode message: $msg")
    }
  }
}
