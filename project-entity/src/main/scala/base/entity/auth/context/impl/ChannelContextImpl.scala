/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:13 PM
 */

package base.entity.auth.context.impl

import base.entity.auth.context.{ AuthContext, ChannelContext, PushChannel }

/**
 * {{ Describe the high level purpose of ChannelContext here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
final case class ChannelContextImpl(private var _authCtx: AuthContext,
                                    pushChannel: Option[PushChannel] = None)
    extends ChannelContext {

  def authCtx = _authCtx
  def authCtx_=(authContext: AuthContext) {
    _authCtx = authContext
  }

}

object ChannelContextImpl {

  def apply(authCtx: AuthContext, pushChannel: PushChannel): ChannelContext =
    apply(authCtx, Option(pushChannel))

}
