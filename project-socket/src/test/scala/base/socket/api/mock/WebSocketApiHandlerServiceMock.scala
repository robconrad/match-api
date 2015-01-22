/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/21/15 9:19 PM
 */

package base.socket.api.mock

import base.socket.api.impl.{ WebSocketApiHandlerServiceImpl, WebSocketChannelInitializer }
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame

/**
 * {{ Describe the high level purpose of SocketApiHandlerServiceMock here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
@Sharable
class WebSocketApiHandlerServiceMock(channelReadResponse: Option[String] = None)
    extends WebSocketApiHandlerServiceImpl {

  override def read(msg: String)(implicit ctx: ChannelHandlerContext) {
    debug("received '%s'", msg)
    channelReadResponse.foreach { msg =>
      debug("writing '%s'", msg)
      val encoded = new TextWebSocketFrame(msg)
      ctx.write(encoded)
      ctx.flush()
    }
  }

  override def makeInitializer = new WebSocketChannelInitializer(this)

}
