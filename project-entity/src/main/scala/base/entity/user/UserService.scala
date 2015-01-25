/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 8:51 AM
 */

package base.entity.user

import java.util.UUID

import base.common.service.{ Service, ServiceCompanion }
import base.entity.auth.context.ChannelContext
import base.entity.error.ApiError
import base.entity.group.model.GroupModel
import base.entity.kv.Key.Pipeline
import base.entity.user.UserService.{ GetGroups, GetUser, GetUsers }
import base.entity.user.model.UserModel

import scala.concurrent.Future

/**
 * CRUD, etc.
 * @author rconrad
 */
trait UserService extends Service {

  final val serviceManifest = manifest[UserService]

  def getUser(userId: UUID)(implicit p: Pipeline, channelCtx: ChannelContext): GetUser

  def getUsers(userId: UUID, userIds: List[UUID])(implicit p: Pipeline, channelCtx: ChannelContext): GetUsers

  def getGroups(userId: UUID)(implicit p: Pipeline, channelCtx: ChannelContext): GetGroups

}

object UserService extends ServiceCompanion[UserService] {

  type GetUser = Future[Either[ApiError, UserModel]]
  type GetUsers = Future[Either[ApiError, List[UserModel]]]
  type GetGroups = Future[Either[ApiError, List[GroupModel]]]

}
