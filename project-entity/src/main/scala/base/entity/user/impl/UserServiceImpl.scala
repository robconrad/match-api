/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 9:03 PM
 */

package base.entity.user.impl

import java.util.UUID

import base.common.service.ServiceImpl
import base.entity.auth.context.AuthContext
import base.entity.error.ApiError
import base.entity.group.model.GroupModel
import base.entity.group.{ GroupService, UserService }
import base.entity.kv.Key._
import base.entity.kv.{ KeyId, SetKey, StringKey }
import base.entity.service.CrudErrorImplicits
import base.entity.user.impl.UserServiceImpl.Errors
import base.entity.user.kv.{ UserGroupsKeyService, UserUserLabelKeyService }
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

  def getUser(userId: UUID)(implicit p: Pipeline, authCtx: AuthContext) = {
    val key = UserUserLabelKeyService().make(authCtx.userId, userId)
    getUser(userId, key)
  }

  private[impl] def getUser(userId: UUID, key: StringKey)(implicit p: Pipeline, authCtx: AuthContext) =
    key.get.map { label =>
      Right(UserModel(userId, label))
    }

  def getUsers(userIds: List[UUID])(implicit p: Pipeline, authCtx: AuthContext) = {
    val futures = userIds.map { userId =>
      UserUserLabelKeyService().make(authCtx.userId, userId).get.map { label =>
        UserModel(userId, label)
      }
    }
    Future.sequence(futures).map(Right.apply)
  }

  def getGroups(userId: UUID)(implicit p: Pipeline, authCtx: AuthContext) = {
    val key = UserGroupsKeyService().make(KeyId(userId))
    getGroups(userId, key)
  }

  private[impl] def getGroups(userId: UUID, key: SetKey)(implicit p: Pipeline, authCtx: AuthContext) = {
    key.members().flatMap { groupIds =>
      val futures = groupIds.map { groupId =>
        GroupService().getGroup(UUID.fromString(groupId))
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

    lazy val notAllGroupsReturned = Left(ApiError(externalErrorText, InternalServerError, "not all groups returned"))

  }

}
