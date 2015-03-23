/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/22/15 6:00 PM
 */

package base.entity.user.impl

import java.util.UUID

import base.common.service.ServiceImpl
import base.entity.auth.context.ChannelContext
import base.entity.error.ApiErrorService
import base.entity.group.GroupService
import base.entity.group.model.GroupModel
import base.entity.kv.MakeKey
import base.entity.service.CrudErrorImplicits
import base.entity.user.UserService
import base.entity.user.UserService.GetGroups
import base.entity.user.impl.UserServiceImpl.Errors
import base.entity.user.kv._
import base.entity.user.model.UserModel
import spray.http.StatusCodes._

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of UserServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class UserServiceImpl extends ServiceImpl with UserService with MakeKey {

  def getUser(userId: UUID)(implicit channelCtx: ChannelContext) = {
    val key = make[UserKey](userId)
    getUser(userId, key)
  }

  private[impl] def getUser(userId: UUID, key: UserKey)(implicit channelCtx: ChannelContext) =
    key.getNameAttributes.map { name =>
      Right(UserModel(userId, name.pictureUrl, name.name))
    }

  def getUsers(userId: UUID, userIds: List[UUID])(implicit channelCtx: ChannelContext) = {
    getUsersFromKey(userId, userIds)
  }

  private[impl] def getUsersFromKey(contextUserId: UUID,
                                    userIds: List[UUID])(implicit channelCtx: ChannelContext) = {
    val futures = userIds.map { userId =>
      make[UserKey](userId).getNameAttributes.map { name =>
        UserModel(userId, name.pictureUrl, name.name)
      }
    }
    Future.sequence(futures).map(Right.apply)
  }

  def getGroups(userId: UUID)(implicit channelCtx: ChannelContext) = {
    getGroupsFromKey(userId, make[UserGroupsKey](userId))
  }

  private[impl] def getGroupsFromKey(userId: UUID,
                                     key: UserGroupsKey)(implicit channelCtx: ChannelContext): GetGroups = {
    key.members flatMap { groupIds =>
      getGroupsFromIds(userId, groupIds)
    }
  }

  def getPendingGroups(userId: UUID)(implicit channelCtx: ChannelContext) = {
    getPendingGroupsFromKey(userId, make[UserGroupsInvitedKey](userId))
  }

  private[impl] def getPendingGroupsFromKey(userId: UUID,
                                            key: UserGroupsInvitedKey)(implicit channelCtx: ChannelContext) = {
    key.members flatMap { groupIds =>
      getGroupsFromIds(userId, groupIds)
    }
  }

  private[impl] def getGroupsFromIds(userId: UUID,
                                     groupIds: Set[UUID])(implicit channelCtx: ChannelContext): GetGroups = {
    val futures = groupIds.map { groupId =>
      GroupService().getGroup(userId, groupId, hydrate = true)
    }
    Future.sequence(futures).map { eithers =>
      lazy val errors = eithers.collect { case Left(error) => error }
      lazy val groups = eithers.collect { case Right(Some(group)) => group }

      (errors.size == 0, groups.size == groupIds.size) match {
        case (true, true) => Right(groups.toList)
        case (false, _)   => Left(errors.head)
        case (_, false)   => Errors.notAllGroupsReturned
      }
    }
  }

}

object UserServiceImpl {

  object Errors extends CrudErrorImplicits[Iterable[GroupModel]] {

    lazy val notAllGroupsReturned =
      Left(ApiErrorService().statusCodeSeed(externalErrorText, InternalServerError, "not all groups returned"))

  }

}
