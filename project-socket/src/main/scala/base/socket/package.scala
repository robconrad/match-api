/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/8/15 5:50 PM
 */

package base

import base.entity.auth.context.AuthContext
import base.socket.api.impl.ChannelAttributes
import base.socket.logging.LoggableChannelInfo
import io.netty.channel._

package object socket {

  implicit class BlackjackNettyChannel(val ch: Channel) extends LoggableChannelInfo {
    def authCtx = Option(ch.attr(ChannelAttributes.authCtx).get)
    def authCtx_=(a: AuthContext) {
      ch.attr(ChannelAttributes.authCtx).set(a)
    }

    def remoteAddress = ch.remoteAddress().toString
  }
}
