/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 9:15 AM
 */

package base.entity.user.impl

import base.common.random.RandomService
import base.common.random.mock.RandomServiceMock
import base.common.service.{ Services, TestServices }
import base.common.time.mock.TimeServiceConstantMock
import base.entity.auth.context.ChannelContextDataFactory
import base.entity.command.impl.CommandServiceImplTest
import base.entity.sms.mock.SmsServiceMock
import base.entity.user.impl.VerifyPhoneCommandServiceImpl._
import base.entity.user.kv._
import base.entity.user.model.{ VerifyPhoneModel, VerifyPhoneResponseModel }

import scala.concurrent.Future

/**
 * Responsible for testing the logic and side effects of the Impl
 * (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class VerifyPhoneCommandServiceImplTest extends CommandServiceImplTest {

  val codeLength = 6

  val service = new VerifyPhoneCommandServiceImpl(codeLength, "body %s")

  private val code = "Some Code"
  private val phone = "555-1234"

  private val randomMock = new RandomServiceMock()

  private implicit val channelCtx = ChannelContextDataFactory.userAuth
  private implicit val model = VerifyPhoneModel(phone, code)

  override def beforeAll() {
    super.beforeAll()
    Services.register(new SmsServiceMock(result = true))
    Services.register(TimeServiceConstantMock)
    Services.register(randomMock)
  }

  private def command(implicit input: VerifyPhoneModel) = new service.VerifyCommand(input)

  test("without perms") {
    assertPermException(channelCtx => {
      service.execute(model)(channelCtx)
    })
  }

  test("success") {
    val userId = RandomService().uuid
    val phoneKey = PhoneKeyService().make(phone)

    assert(phoneKey.setUserId(userId).await())
    assert(phoneKey.setCode(code).await())

    assert(service.innerExecute(model).await() == Right(VerifyPhoneResponseModel(phone)))

    val userKey = UserKeyService().make(userId)
    assert(userKey.getPhoneVerified.await() == (Option(phone), Option(true)))
  }

  test("failed to get phone verification code") {
    val key = mock[PhoneKey]
    key.getCode _ expects () returning Future.successful(None)
    assert(command.phoneGetCode(key).await() == Errors.codeMissing.await())
  }

  test("failed to validate phone verification code") {
    val key = mock[PhoneKey]
    val code = model.code + "munge"
    assert(command.phoneVerify(key, code).await() == Errors.codeValidation.await())
  }

  test("failed to get userId") {
    val key = mock[PhoneKey]
    key.getUserId _ expects () returning Future.successful(None)
    assert(command.phoneGetUserId(key).await() == Errors.userIdMissing.await())
  }

  test("failed to set user attributes") {
    val key = mock[UserKey]
    key.setPhoneVerified _ expects (*, *) returning Future.successful(false)
    assert(command.userSet(key).await() == Errors.userSetFailed.await())
  }

  test("send verify sms") {
    assert(service.sendVerifySms(phone, code).await())
  }

  test("make verify code") {
    val randomMock = new RandomServiceMock()
    val unregister = TestServices.register(randomMock)
    val nextMd5 = randomMock.nextMd5()
    val code = service.makeVerifyCode()
    assert(code == nextMd5.toString.substring(0, codeLength).toUpperCase)
    unregister()
  }

  test("validate verify codes") {
    val code1 = "abc "
    val code2 = " ABc"
    val code3 = "ABcd"
    assert(service.validateVerifyCodes(code1, code2))
    assert(!service.validateVerifyCodes(code1, code3))
  }

}
