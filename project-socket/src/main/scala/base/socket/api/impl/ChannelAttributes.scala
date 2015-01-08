/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/7/15 9:05 PM
 */

package base.socket.api.impl

import io.netty.util.AttributeKey

/**
 * {{ Describe the high level purpose of ChannelAttributes here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object ChannelAttributes {

  val userId = AttributeKey.valueOf[Int]("userId")

}
