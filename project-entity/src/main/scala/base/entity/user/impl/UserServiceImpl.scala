/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 9:57 PM
 */

package base.entity.user.impl

import base.common.service.ServiceImpl
import base.common.time.DateTimeHelper
import base.entity.error.ApiError
import base.entity.logging.AuthLoggable
import base.entity.service.CrudServiceImplHelper
import base.entity.user.UserService
import base.entity.user.model._

import scala.concurrent.Future

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class UserServiceImpl
    extends ServiceImpl with UserService with CrudServiceImplHelper[UserModel] with AuthLoggable with DateTimeHelper {

  def register(input: RegisterModel) = Future.successful(Left(ApiError("not implemented")))

  def verify(input: VerifyModel) = Future.successful(Left(ApiError("not implemented")))

  def login(input: LoginModel) = Future.successful(Left(ApiError("not implemented")))

}
