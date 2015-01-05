/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 2:41 PM
 */

package base

import base.socket.logging.LoggableChannelInfo
import base.socket.netty.ChannelAttributes
import io.netty.channel._

package object socket {

  implicit class BlackjackNettyChannel(val ch: Channel) extends LoggableChannelInfo {
    def userId: Int = Option[Int](ch.attr(ChannelAttributes.userId).get) getOrElse -1
    def userId_=(a: Int) {
      ch.attr(ChannelAttributes.userId).set(a)
    }

    def remoteAddress = ch.remoteAddress().toString
  }
}
