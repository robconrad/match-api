/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 2:21 PM
 */

package base.entity.user

import base.common.service.{ Service, ServiceCompanion }
import base.entity.auth.context.AuthContext
import base.entity.error.ApiError
import base.entity.user.VerifyService.VerifyResponse
import base.entity.user.model._

import scala.concurrent.Future

/**
 * User CRUD, etc.
 * @author rconrad
 */
trait VerifyService extends Service {

  final def serviceManifest = manifest[VerifyService]

  def verify(input: VerifyModel)(implicit authCtx: AuthContext): VerifyResponse

  def sendVerifySms(phone: String, code: String): Future[Boolean]

  def makeVerifyCode(): String

  def validateVerifyCodes(code1: String, code2: String): Boolean

}

object VerifyService extends ServiceCompanion[VerifyService] {

  type VerifyResponse = Future[Either[ApiError, VerifyResponseModel]]

}
