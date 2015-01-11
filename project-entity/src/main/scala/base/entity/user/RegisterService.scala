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
import base.entity.user.RegisterService.RegisterResponse
import base.entity.user.model._

import scala.concurrent.Future

/**
 * User CRUD, etc.
 * @author rconrad
 */
trait RegisterService extends Service {

  final def serviceManifest = manifest[RegisterService]

  def register(input: RegisterModel)(implicit authCtx: AuthContext): RegisterResponse

}

object RegisterService extends ServiceCompanion[RegisterService] {

  type RegisterResponse = Future[Either[ApiError, RegisterResponseModel]]

}
