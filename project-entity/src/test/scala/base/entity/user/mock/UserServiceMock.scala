/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 5:57 PM
 */

package base.entity.user.mock

import java.util.UUID

import base.entity.auth.context.AuthContext
import base.entity.error.ApiError
import base.entity.group.UserService
import base.entity.group.UserService.{ GetGroups, GetUsers, GetUser }
import base.entity.kv.Key.Pipeline

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of UserServiceMock here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class UserServiceMock(getUserResult: GetUser = Future.successful(Left(ApiError("not impl"))),
                      getUsersResult: GetUsers = Future.successful(Right(List())),
                      getGroupsResult: GetGroups = Future.successful(Right(List())))
    extends UserService {

  def getUser(userId: UUID)(implicit p: Pipeline, authCtx: AuthContext) = getUserResult

  def getUsers(userIds: Iterable[UUID])(implicit p: Pipeline, authCtx: AuthContext) = getUsersResult

  def getGroups(userId: UUID)(implicit p: Pipeline, authCtx: AuthContext) = getGroupsResult

}
