/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/15/15 10:19 AM
 */

package base.socket.api.impl

import io.netty.buffer.{ ByteBuf, Unpooled }
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel._
import io.netty.util.CharsetUtil

import scala.concurrent.duration.FiniteDuration

@Sharable
class RawSocketApiHandlerServiceImpl extends SocketApiHandlerServiceImpl {

  override def channelRead(ctx: ChannelHandlerContext, msg: Object) {
    read(msg.asInstanceOf[ByteBuf].toString(CharsetUtil.UTF_8))(ctx)
  }

  def write(json: String)(implicit ctx: ChannelHandlerContext) = {
    debug("message sent: %s", json)
    val terminated = json.length > 0 && json.last == '\n' match {
      case true  => json
      case false => json + "\r\n"
    }
    val encoded = Unpooled.copiedBuffer(terminated, CharsetUtil.UTF_8)
    ctx.writeAndFlush(encoded)
  }

  def makeInitializer(idleTimeout: FiniteDuration) =
    new RawSocketChannelInitializer(this, idleTimeout.toSeconds.toInt)

}
