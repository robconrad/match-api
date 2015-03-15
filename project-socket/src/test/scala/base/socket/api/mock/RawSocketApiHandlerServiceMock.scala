/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/15/15 10:19 AM
 */

package base.socket.api.mock

import base.common.logging.Loggable
import base.socket.api.SocketApiHandlerService
import base.socket.api.impl.RawSocketChannelInitializer
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ ChannelHandlerContext, ChannelInboundHandlerAdapter }
import io.netty.util.CharsetUtil

import scala.concurrent.duration.FiniteDuration

/**
 * {{ Describe the high level purpose of SocketApiHandlerServiceMock here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
@Sharable // scalastyle:off null
class RawSocketApiHandlerServiceMock(channelReadResponse: Option[String] = None)
    extends ChannelInboundHandlerAdapter
    with SocketApiHandlerService
    with Loggable {

  def stop() {}

  def write(json: String)(implicit ctx: ChannelHandlerContext) = null

  override def channelRead(ctx: ChannelHandlerContext, msg: Any) {
    debug("received '%s'", msg)
    channelReadResponse.foreach { msg =>
      debug("writing '%s'", msg)
      val terminated = msg.length > 0 && msg.last == '\n' match {
        case true  => msg
        case false => msg + "\r\n"
      }
      val encoded = Unpooled.copiedBuffer(terminated, CharsetUtil.UTF_8)
      ctx.write(encoded)
      ctx.flush()
    }
  }

  def makeInitializer(idleTimeout: FiniteDuration) =
    new RawSocketChannelInitializer(this, idleTimeout.toSeconds.toInt)

}
