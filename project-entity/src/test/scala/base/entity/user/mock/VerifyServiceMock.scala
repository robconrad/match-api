/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 5:37 PM
 */

package base.entity.user.mock

import base.common.service.ServiceImpl
import base.entity.auth.context.AuthContext
import base.entity.error.ApiError
import base.entity.user.VerifyService
import base.entity.user.VerifyService.VerifyResponse
import base.entity.user.model.VerifyModel

import scala.concurrent.Future

/**
 * Fake UserService will do whatever you like
 * @author rconrad
 */
class VerifyServiceMock(verifyResult: VerifyResponse = Future.successful(Left(ApiError("not implemented"))),
                        sendVerifySmsResult: Future[Boolean] = Future.successful(true),
                        validateVerifyCodesResult: Boolean = true,
                        makeVerifyCodeResult: String = "code")
    extends ServiceImpl with VerifyService {

  def verify(input: VerifyModel)(implicit authCtx: AuthContext) = verifyResult

  def sendVerifySms(phone: String, code: String) = sendVerifySmsResult

  def validateVerifyCodes(code1: String, code2: String) = validateVerifyCodesResult

  def makeVerifyCode() = makeVerifyCodeResult

}
