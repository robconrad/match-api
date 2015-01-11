/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 1:37 PM
 */

package base.entity.user.impl

import base.common.random.mock.RandomServiceMock
import base.common.service.{ TestServices, Services }
import base.entity.api.ApiVersions
import base.entity.auth.context.AuthContextDataFactory
import base.entity.kv.mock.{ HashKeyMock, IntKeyMock, KeyMock }
import base.entity.kv.{ KeyId, KvService, KvTest }
import base.entity.service.EntityServiceTest
import base.entity.sms.mock.SmsServiceMock
import base.entity.user.UserKeyFactories.PhoneCooldownKeyFactory
import base.entity.user.impl.RegisterServiceImpl._
import base.entity.user.model.{ RegisterModel, RegisterResponseModel }

import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * Responsible for testing the logic and side effects of the Impl
 *  (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class RegisterServiceImplTest extends EntityServiceTest with KvTest {

  private val codeLength = 6

  val service = new RegisterServiceImpl(10.minutes, codeLength, "body")

  private implicit val registerModel = RegisterModel(ApiVersions.V01, "bob")
  private implicit val pipeline = KvService().pipeline
  private implicit val authCtx = AuthContextDataFactory.userAuth

  override def beforeAll() {
    super.beforeAll()
    Services.register(new SmsServiceMock(result = true))
  }

  test("without perms") {
    assertPermException(authCtx => {
      service.register(registerModel)(authCtx)
    })
  }

  test("success") {
    assert(service.register(registerModel).await() == Right(RegisterResponseModel()))
  }

  test("phone cooldown in effect") {
    assert(PhoneCooldownKeyFactory().make(KeyId(registerModel.phone)).set(1).await())
    assert(service.phoneCooldownExists().await() == externalErrorPhoneResponse.await())
  }

  test("failed to set phone cooldown") {
    val key = new IntKeyMock(setResult = Future.successful(false))
    assert(service.phoneCooldownSet(key).await() == internalErrorSetPhoneCooldownResponse.await())
  }

  test("failed to expire phone cooldown") {
    val key = new IntKeyMock(keyMock = new KeyMock(expireResult = Future.successful(false)))
    assert(service.phoneCooldownExpire(key).await() == internalErrorExpirePhoneCooldownResponse.await())
  }

  test("failed to set phone updates") {
    val key = new HashKeyMock(setMultiResult = Future.successful(false))
    assert(service.phoneKeySet(key).await() == internalErrorPhoneUpdatesResponse.await())
  }

  test("failed to send sms") {
    val unregister = TestServices.register(new SmsServiceMock(result = false))
    assert(service.smsSend(code = "code").await() == Left(internalErrorSmsFailedResponse))
    unregister()
  }

  test("make verify code") {
    val randomMock = new RandomServiceMock()
    val unregister = TestServices.register(randomMock)
    val nextMd5 = randomMock.nextMd5()
    val code = service.makeVerifyCode()
    assert(code == nextMd5.toString.substring(0, codeLength).toUpperCase)
    unregister()
  }

}
