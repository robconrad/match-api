/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 4:15 PM
 */

package base.socket.api

import base.common.logging.Loggable
import base.entity.auth.context.{ NoAuthContext, AuthContext }
import base.socket.logging.LoggableChannelInfo
import io.netty.channel._
import io.netty.util.AttributeKey

// scalastyle:off null
package object impl {

  private val authCtxAttr = AttributeKey.valueOf[AuthContext]("authCtx")

  implicit class ChannelInfo(val ch: Channel) extends LoggableChannelInfo with Loggable {

    def authCtx = {
      ch.attr(authCtxAttr).get match {
        case null    => NoAuthContext
        case authCtx => authCtx
      }
    }
    def authCtx_=(a: AuthContext) {
      ch.attr(authCtxAttr).set(a)
    }

    def remoteAddress = ch.remoteAddress().toString

  }

}
