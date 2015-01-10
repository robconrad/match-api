/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/8/15 5:50 PM
 */

package base.socket.logging

import base.entity.auth.context.AuthContext

/**
 * {{ Describe the high level purpose of LoggableChannelInfo here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait LoggableChannelInfo {

  def authCtx: Option[AuthContext]
  def remoteAddress: String

}
