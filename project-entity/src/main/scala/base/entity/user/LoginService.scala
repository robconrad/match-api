/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 9:49 AM
 */

package base.entity.user

import base.common.service.{ Service, ServiceCompanion }
import base.entity.auth.context.AuthContext
import base.entity.error.ApiError
import base.entity.user.LoginService.LoginResponse
import base.entity.user.model._

import scala.concurrent.Future

/**
 * User CRUD, etc.
 * @author rconrad
 */
trait LoginService extends Service {

  final def serviceManifest = manifest[LoginService]

  def login(input: LoginModel)(implicit authCtx: AuthContext): LoginResponse

}

object LoginService extends ServiceCompanion[LoginService] {

  type LoginResponse = Future[Either[ApiError, LoginResponseModel]]

}
