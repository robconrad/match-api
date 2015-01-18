/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 8:06 PM
 */

package base.entity.group

import java.util.UUID

import base.common.service.{ Service, ServiceCompanion }
import base.entity.auth.context.AuthContext
import base.entity.error.ApiError
import base.entity.group.UserService.{ GetGroups, GetUser, GetUsers }
import base.entity.group.model.GroupModel
import base.entity.kv.Key.Pipeline
import base.entity.user.model.UserModel

import scala.concurrent.Future

/**
 * CRUD, etc.
 * @author rconrad
 */
trait UserService extends Service {

  final def serviceManifest = manifest[UserService]

  def getUser(userId: UUID)(implicit p: Pipeline, authCtx: AuthContext): GetUser

  def getUsers(userIds: Iterable[UUID])(implicit p: Pipeline, authCtx: AuthContext): GetUsers

  def getGroups(userId: UUID)(implicit p: Pipeline, authCtx: AuthContext): GetGroups

}

object UserService extends ServiceCompanion[UserService] {

  type GetUser = Future[Either[ApiError, UserModel]]
  type GetUsers = Future[Either[ApiError, Iterable[UserModel]]]
  type GetGroups = Future[Either[ApiError, Iterable[GroupModel]]]

}
