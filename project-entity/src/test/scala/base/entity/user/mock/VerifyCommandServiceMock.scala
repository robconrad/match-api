/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 12:17 PM
 */

package base.entity.user.mock

import base.common.service.ServiceImpl
import base.entity.auth.context.AuthContext
import base.entity.error.ApiError
import base.entity.user.VerifyCommandService
import base.entity.user.VerifyCommandService.VerifyResponse
import base.entity.user.model.VerifyModel

import scala.concurrent.Future

/**
 * Fake UserService will do whatever you like
 * @author rconrad
 */
class VerifyCommandServiceMock(verifyResult: VerifyResponse = Future.successful(Left(ApiError("not implemented"))),
                               sendVerifySmsResult: Future[Boolean] = Future.successful(true),
                               validateVerifyCodesResult: Boolean = true,
                               makeVerifyCodeResult: String = "code")
    extends ServiceImpl with VerifyCommandService {

  def execute(input: VerifyModel)(implicit authCtx: AuthContext) = verifyResult

  def sendVerifySms(phone: String, code: String) = sendVerifySmsResult

  def validateVerifyCodes(code1: String, code2: String) = validateVerifyCodesResult

  def makeVerifyCode() = makeVerifyCodeResult

}
