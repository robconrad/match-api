/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/13/15 5:19 PM
 */

package base.entity.user

import base.entity.auth.context.AuthContext
import base.entity.command.{ CommandService, CommandServiceCompanion }
import base.entity.error.ApiError
import base.entity.user.VerifyCommandService.VerifyResponse
import base.entity.user.model._

import scala.concurrent.Future

/**
 * User CRUD, etc.
 * @author rconrad
 */
trait VerifyCommandService extends CommandService {

  final def serviceManifest = manifest[VerifyCommandService]

  def verify(input: VerifyModel)(implicit authCtx: AuthContext): VerifyResponse

  def sendVerifySms(phone: String, code: String): Future[Boolean]

  def makeVerifyCode(): String

  def validateVerifyCodes(code1: String, code2: String): Boolean

}

object VerifyCommandService extends CommandServiceCompanion[VerifyCommandService] {

  type VerifyResponse = Future[Either[ApiError, VerifyResponseModel]]

}
