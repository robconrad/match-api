/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 12:06 AM
 */

package base.entity.auth.context

import base.common.random.RandomService
import base.entity.auth.context.impl.ChannelContextImpl
import base.entity.command.model.CommandModel
import base.entity.perm.PermSetGroups
import base.entity.user.User

import scala.concurrent.Future

/**
 * Creates various fake auth contexts
 * @author rconrad
 */
object ChannelContextDataFactory {

  val emptyUserAuth = ChannelContextImpl(new UserAuthContext {
    val user = None
    val perms = PermSetGroups.none
  }, None)

  val userAuth = ChannelContextImpl(new StandardUserAuthContext(User(RandomService().uuid)), Option(new PushChannel {
    def push[T <: CommandModel[_]](command: T)(implicit m: Manifest[T]) = Future.successful(true)
  }))

}
