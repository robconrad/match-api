/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 8:59 AM
 */

package base.entity.user.impl

import java.util.UUID

import base.common.service.ServiceImpl
import base.entity.auth.context.ChannelContext
import base.entity.error.ApiErrorService
import base.entity.group.GroupService
import base.entity.group.model.GroupModel
import base.entity.kv.Key._
import base.entity.service.CrudErrorImplicits
import base.entity.user.UserService
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
class UserServiceImpl extends ServiceImpl with UserService {

  def getUser(userId: UUID)(implicit p: Pipeline, channelCtx: ChannelContext) = {
    val key = UserKeyService().make(userId)
    getUser(userId, key)
  }

  private[impl] def getUser(userId: UUID, key: UserKey)(implicit p: Pipeline, channelCtx: ChannelContext) =
    key.getName.map { name =>
      Right(UserModel(userId, name))
    }

  def getUsers(userId: UUID, userIds: List[UUID])(implicit p: Pipeline, channelCtx: ChannelContext) = {
    getUsers(userId, userIds, UserKeyService())
  }

  private[impl] def getUsers(contextUserId: UUID, userIds: List[UUID],
                             keyService: UserKeyService)(implicit p: Pipeline, channelCtx: ChannelContext) = {
    val futures = userIds.map { userId =>
      keyService.make(userId).getName.map { name =>
        UserModel(userId, name)
      }
    }
    Future.sequence(futures).map(Right.apply)
  }

  def getGroups(userId: UUID)(implicit p: Pipeline, channelCtx: ChannelContext) = {
    val key = UserGroupsKeyService().make(userId)
    getGroups(userId, key)
  }

  private[impl] def getGroups(userId: UUID, key: UserGroupsKey)(implicit p: Pipeline, channelCtx: ChannelContext) = {
    key.members().flatMap { groupIds =>
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

}

object UserServiceImpl {

  object Errors extends CrudErrorImplicits[Iterable[GroupModel]] {

    lazy val notAllGroupsReturned =
      Left(ApiErrorService().statusCodeSeed(externalErrorText, InternalServerError, "not all groups returned"))

  }

}
