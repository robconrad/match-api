/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/24/15 6:43 PM
 */

package base.entity.auth.context

/**
 * {{ Describe the high level purpose of ChannelContext here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait ChannelContext {

  def authCtx: AuthContext
  def authCtx_=(authContext: AuthContext)
  def pushChannel: Option[PushChannel]

}
