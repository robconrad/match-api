/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 7:08 PM
 */

package base.entity.user.impl

import java.util.UUID

import base.common.random.mock.RandomServiceMock
import base.common.service.{ Services, TestServices }
import base.common.time.mock.TimeServiceConstantMock
import base.entity.api.ApiVersions
import base.entity.auth.context.AuthContextDataFactory
import base.entity.kv.KeyProps.{ UpdatedProp, CreatedProp }
import base.entity.kv.mock.{ HashKeyMock, IntKeyMock, KeyMock }
import base.entity.kv.{ KeyId, KvService, KvTest }
import base.entity.service.EntityServiceTest
import base.entity.sms.mock.SmsServiceMock
import base.entity.user.UserKeyFactories.{ UserKeyFactory, PhoneKeyFactory, PhoneCooldownKeyFactory }
import base.entity.user.UserKeyProps.{ CodeProp, UserIdProp }
import base.entity.user.impl.RegisterServiceImpl._
import base.entity.user.mock.VerifyServiceMock
import base.entity.user.model.{ RegisterModel, RegisterResponseModel }

import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * Responsible for testing the logic and side effects of the Impl
 *  (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class RegisterServiceImplTest extends EntityServiceTest with KvTest {

  val service = new RegisterServiceImpl(10.minutes)

  private val phone = "555-1234"
  private val verifyCode = "verifyCode"

  private val randomMock = new RandomServiceMock()
  private val verifyMock = new VerifyServiceMock(makeVerifyCodeResult = verifyCode)

  private implicit val registerModel = RegisterModel(ApiVersions.V01, phone)
  private implicit val pipeline = KvService().pipeline
  private implicit val authCtx = AuthContextDataFactory.userAuth

  override def beforeAll() {
    super.beforeAll()
    Services.register(TimeServiceConstantMock)
    Services.register(randomMock)
    Services.register(verifyMock)
  }

  private def assertSuccessConditions(userId: UUID) {
    val phoneCooldownKey = PhoneCooldownKeyFactory().make(KeyId(phone))
    assert(phoneCooldownKey.get().await() == Option(phoneCooldownValue))
    assert(phoneCooldownKey.ttl().await().getOrElse(-1L) > 0L)

    val phoneKey = PhoneKeyFactory().make(KeyId(phone))
    assert(phoneKey.getDateTime(CreatedProp).await().exists(_.isEqual(TimeServiceConstantMock.now)))
    assert(phoneKey.getDateTime(UpdatedProp).await().exists(_.isEqual(TimeServiceConstantMock.now)))
    assert(phoneKey.getId(UserIdProp).await().contains(userId))
    assert(phoneKey.getString(CodeProp).await().contains(verifyCode))

    val userKey = UserKeyFactory().make(KeyId(userId))
    assert(userKey.getDateTime(CreatedProp).await().exists(_.isEqual(TimeServiceConstantMock.now)))
  }

  test("without perms") {
    assertPermException(authCtx => {
      service.register(registerModel)(authCtx)
    })
  }

  test("success - no existing phone or user") {
    val userId = randomMock.nextUuid()
    assert(service.register(registerModel).await() == Right(RegisterResponseModel()))
    assertSuccessConditions(userId)
  }

  test("success - existing phone and user") {
    val userId = randomMock.nextUuid()
    assert(service.register(registerModel).await() == Right(RegisterResponseModel()))
    assertSuccessConditions(userId)

    val phoneCooldownKey = PhoneCooldownKeyFactory().make(KeyId(phone))
    assert(phoneCooldownKey.del().await())

    assert(service.register(registerModel).await() == Right(RegisterResponseModel()))
    assertSuccessConditions(userId)
  }

  test("phone cooldown in effect") {
    val key = PhoneCooldownKeyFactory().make(KeyId(registerModel.phone))
    assert(key.set(1).await())
    assert(service.phoneCooldownExists(key).await() == externalErrorPhoneResponse.await())
  }

  test("failed to set phone cooldown") {
    val keyMock = new IntKeyMock(setResult = Future.successful(false))
    assert(service.phoneCooldownSet(keyMock).await() == internalErrorSetPhoneCooldownResponse.await())
  }

  test("failed to expire phone cooldown") {
    val key = new IntKeyMock(keyMock = new KeyMock(expireResult = Future.successful(false)))
    assert(service.phoneCooldownExpire(key).await() == internalErrorExpirePhoneCooldownResponse.await())
  }

  test("failed to create user") {
    val key = new HashKeyMock(setResult = Future.successful(false))
    assert(service.userKeyCreate(key).await() == internalErrorUserCreateResponse.await())
  }

  test("failed to set phone updates") {
    val key = new HashKeyMock(setMultiResult = Future.successful(false))
    assert(service.phoneKeyUpdates(key).await() == internalErrorPhoneUpdatesResponse.await())
  }

  test("failed to send sms") {
    val unregister = TestServices.register(new VerifyServiceMock(sendVerifySmsResult = Future.successful(false)))
    assert(service.smsSend(code = "code").await() == Left(internalErrorSmsFailedResponse))
    unregister()
  }

}
