/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/21/15 9:02 PM
 */

package base.socket.api

import io.netty.channel.Channel
import io.netty.util.AttributeKey

/**
 * {{ Describe the high level purpose of package here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
package object test {

  private val lastMessageAttr = AttributeKey.valueOf[Option[String]]("lastMessage")

  implicit class ChannelInfo(ch: Channel) {

    // scalastyle:off null
    def lastMessage = {
      ch.attr(lastMessageAttr).get() match {
        case null => None
        case x    => x
      }
    }

    def lastMessage_=(h: Option[String]) {
      ch.attr(lastMessageAttr).set(h)
    }

  }

}
