/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/3/15 6:20 PM
 */

package base.socket.logging

/**
 * {{ Describe the high level purpose of LoggableChannelInfo here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait LoggableChannelInfo {

  def userId: Int
  def remoteAddress: String

}
