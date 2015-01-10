/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/9/15 10:03 PM
 */

package base.socket.api.impl

import base.common.lib.Tryo
import base.socket._
import base.socket.logging.SocketLoggable
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import io.netty.util.CharsetUtil
import org.json4s.native.JsonMethods

/**
 * {{ Describe the high level purpose of JsonDecoder here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
@Sharable
object JsonDecoder extends MessageToMessageDecoder[ByteBuf] with SocketLoggable {
  override protected def decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: java.util.List[Object]) {
    val msgStr = msg.toString(CharsetUtil.UTF_8)
    if (isDebugEnabled) debug("message received: " + msgStr)(ctx)
    out.add(Tryo(JsonMethods.parse(msgStr)))
  }
}
