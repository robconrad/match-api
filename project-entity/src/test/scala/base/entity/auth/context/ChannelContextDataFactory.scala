/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 7:58 PM
 */

package base.entity.auth.context

import java.util.UUID

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
    val groups = Set[UUID]()
  }, None)

  val godPerms = ChannelContextImpl(new UserAuthContext {
    val user = Option(User(RandomService().uuid))
    val perms = PermSetGroups.god
    val groups = Set[UUID]()
  }, None)

  def userAuth(groups: UUID*) =
    ChannelContextImpl(new StandardUserAuthContext(User(RandomService().uuid), groups.toSet), Option(new PushChannel {
      def push[T <: CommandModel[_]](command: T)(implicit m: Manifest[T]) = Future.successful(true)
    }))

}
