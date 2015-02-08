/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/7/15 3:57 PM
 */

package base.entity.user.impl

import java.util.UUID

import base.common.service.ServiceImpl
import base.entity.auth.context.ChannelContext
import base.entity.error.ApiErrorService
import base.entity.group.GroupService
import base.entity.group.model.GroupModel
import base.entity.kv.Key._
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

  def getUser(userId: UUID)(implicit p: Pipeline, channelCtx: ChannelContext) = {
    val key = UserKeyService().make(userId)
    getUser(userId, key)
  }

  private[impl] def getUser(userId: UUID, key: UserKey)(implicit p: Pipeline, channelCtx: ChannelContext) =
    key.getNameAttributes.map { name =>
      Right(UserModel(userId, name.pictureUrl, name.name))
    }

  def getUsers(userId: UUID, userIds: List[UUID])(implicit p: Pipeline, channelCtx: ChannelContext) = {
    getUsersFromKey(userId, userIds, UserKeyService())
  }

  private[impl] def getUsersFromKey(contextUserId: UUID, userIds: List[UUID],
                                    keyService: UserKeyService)(implicit p: Pipeline, channelCtx: ChannelContext) = {
    val futures = userIds.map { userId =>
      keyService.make(userId).getNameAttributes.map { name =>
        UserModel(userId, name.pictureUrl, name.name)
      }
    }
    Future.sequence(futures).map(Right.apply)
  }

  def getGroups(userId: UUID)(implicit p: Pipeline, channelCtx: ChannelContext) = {
    getGroupsFromKey(userId, make[UserGroupsKey](userId))
  }

  private[impl] def getGroupsFromKey(userId: UUID, key: UserGroupsKey)(implicit p: Pipeline,
                                                                       channelCtx: ChannelContext): GetGroups = {
    key.members flatMap { groupIds =>
      getGroupsFromIds(userId, groupIds)
    }
  }

  def getPendingGroups(userId: UUID)(implicit p: Pipeline, channelCtx: ChannelContext) = {
    getPendingGroupsFromKey(userId, make[UserGroupsInvitedKey](userId))
  }

  private[impl] def getPendingGroupsFromKey(userId: UUID,
                                            key: UserGroupsInvitedKey)(implicit p: Pipeline,
                                                                       channelCtx: ChannelContext): GetGroups = {
    key.members flatMap { groupIds =>
      getGroupsFromIds(userId, groupIds)
    }
  }

  private[impl] def getGroupsFromIds(userId: UUID,
                                     groupIds: Set[UUID])(implicit p: Pipeline,
                                                          channelCtx: ChannelContext): GetGroups = {
    val futures = groupIds.map { groupId =>
      GroupService().getGroup(userId, groupId)
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
