/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 10:56 AM
 */

package base.socket.api

import base.common.logging.Loggable
import base.entity.auth.context.ChannelContext
import base.entity.error.ApiErrorService
import base.entity.error.model.ApiError
import base.socket.logging.LoggableChannelInfo
import io.netty.channel._
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker
import io.netty.util.AttributeKey

// scalastyle:off null
package object impl {

  private val channelCtxAttr = AttributeKey.valueOf[ChannelContext]("channelCtx")
  private val handshakerAttr = AttributeKey.valueOf[WebSocketServerHandshaker]("handshaker")

  implicit class ChannelInfo(val ch: Channel) extends LoggableChannelInfo with Loggable {

    def authCtx = {
      channelCtx.authCtx
    }

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

    def remoteAddress = {
      ch.remoteAddress().toString
    }

    def close(apiError: ApiError)(implicit ctx: ChannelHandlerContext): ChannelFuture = {
      close(ApiErrorService().toJson(apiError))
    }

    def close(json: String)(implicit ctx: ChannelHandlerContext): ChannelFuture = {
      val channelFuture = SocketApiHandlerService().write(json)
      if (channelFuture != null) {
        channelFuture.addListener(ChannelFutureListener.CLOSE)
      }
      channelFuture
    }

  }

}
