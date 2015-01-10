/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/8/15 5:17 PM
 */

package base.entity.user

import base.common.service.{ Service, ServiceCompanion }
import base.entity.auth.context.AuthContext
import base.entity.error.ApiError
import base.entity.user.model._

import scala.concurrent.Future

/**
 * User CRUD, etc.
 * @author rconrad
 */
trait UserService extends Service {

  final def serviceManifest = manifest[UserService]

  def register(input: RegisterModel)(implicit authCtx: AuthContext): Future[Either[ApiError, RegisterResponseModel]]

  def verify(input: VerifyModel)(implicit authCtx: AuthContext): Future[Either[ApiError, VerifyResponseModel]]

  def login(input: LoginModel)(implicit authCtx: AuthContext): Future[Either[ApiError, LoginResponseModel]]

}

object UserService extends ServiceCompanion[UserService]
