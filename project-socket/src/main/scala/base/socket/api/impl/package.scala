/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 12:19 AM
 */

package base.socket.api

import base.common.logging.Loggable
import base.entity.auth.context.ChannelContext
import base.socket.logging.LoggableChannelInfo
import io.netty.channel._
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker
import io.netty.util.AttributeKey

// scalastyle:off null
package object impl {

  private val channelCtxAttr = AttributeKey.valueOf[ChannelContext]("channelCtx")
  private val handshakerAttr = AttributeKey.valueOf[WebSocketServerHandshaker]("handshaker")

  implicit class ChannelInfo(val ch: Channel) extends LoggableChannelInfo with Loggable {

    def authCtx = channelCtx.authCtx

    def channelCtx = {
      ch.attr(channelCtxAttr).get()
    }
    def channelCtx_=(c: ChannelContext) {
      ch.attr(channelCtxAttr).set(c)
    }

    def handshaker = {
      ch.attr(handshakerAttr).get()
    }
    def handshaker_=(h: WebSocketServerHandshaker) {
      ch.attr(handshakerAttr).set(h)
    }

    def remoteAddress = ch.remoteAddress().toString

  }

}
