/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 6:10 PM
 */

package base.entity.user.impl

import java.util.UUID

import base.common.service.ServiceImpl
import base.entity.auth.context.AuthContext
import base.entity.error.ApiError
import base.entity.group.UserService
import base.entity.kv.Key._

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of UserServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class UserServiceImpl extends ServiceImpl with UserService {

  def getUser(userId: UUID)(implicit p: Pipeline, authCtx: AuthContext) =
    Future.successful(Left(ApiError("not impl")))

  def getUsers(userIds: Iterable[UUID])(implicit p: Pipeline, authCtx: AuthContext) =
    Future.successful(Left(ApiError("not impl")))

  def getGroups(userId: UUID)(implicit p: Pipeline, authCtx: AuthContext) =
    Future.successful(Left(ApiError("not impl")))

}
