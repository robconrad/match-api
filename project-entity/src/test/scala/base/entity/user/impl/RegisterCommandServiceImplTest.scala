/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/21/15 10:09 PM
 */

package base.entity.user.impl

import java.util.UUID

import base.common.random.mock.RandomServiceMock
import base.common.service.{ Services, TestServices }
import base.common.time.mock.TimeServiceConstantMock
import base.entity.api.ApiVersions
import base.entity.auth.context.AuthContextDataFactory
import base.entity.command.impl.CommandServiceImplTest
import base.entity.kv.Key.Prop
import base.entity.kv.KeyId
import base.entity.kv.KeyProps.{ CreatedProp, UpdatedProp }
import base.entity.kv.mock.KeyLoggerMock
import base.entity.user.VerifyCommandService
import base.entity.user.impl.RegisterCommandServiceImpl._
import base.entity.user.kv.UserKeyProps.{ CodeProp, UserIdProp }
import base.entity.user.kv.impl.{ UserKeyImpl, PhoneKeyImpl }
import base.entity.user.kv.{ PhoneKey, PhoneCooldownKey, PhoneCooldownKeyService }
import base.entity.user.model.{ RegisterModel, RegisterResponseModel }

import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * Responsible for testing the logic and side effects of the Impl
 *  (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class RegisterCommandServiceImplTest extends CommandServiceImplTest {

  val service = new RegisterCommandServiceImpl(10.minutes)

  private val phone = "555-1234"
  private val verifyCode = "verifyCode"

  private val randomMock = new RandomServiceMock()

  private implicit val registerModel = RegisterModel(ApiVersions.V01, phone)
  private implicit val authCtx = AuthContextDataFactory.userAuth

  override def beforeAll() {
    super.beforeAll()
    Services.register(TimeServiceConstantMock)
    Services.register(randomMock)
  }

  private def command(implicit input: RegisterModel) = new service.RegisterCommand(input)

  private def assertSuccessConditions(userId: UUID) {
    val phoneCooldownKey = PhoneCooldownKeyService().make(KeyId(phone))
    assert(phoneCooldownKey.get().await() == Option(phoneCooldownValue))
    assert(phoneCooldownKey.ttl().await().getOrElse(-1L) > 0L)

    val phoneKey = new PhoneKeyImpl(s"phone-$phone", KeyLoggerMock)
    assert(phoneKey.getCreated.await().exists(_.isEqual(TimeServiceConstantMock.now)))
    assert(phoneKey.getUpdated.await().exists(_.isEqual(TimeServiceConstantMock.now)))
    assert(phoneKey.getUserId.await().contains(userId))
    assert(phoneKey.getCode.await().contains(verifyCode))

    val userKey = new UserKeyImpl(s"user-$userId", KeyLoggerMock)
    assert(userKey.getCreated.await().exists(_.isEqual(TimeServiceConstantMock.now)))
  }

  test("without perms") {
    assertPermException(authCtx => {
      service.execute(registerModel)(authCtx)
    })
  }

  test("success - no existing phone or user") {
    val userId = randomMock.nextUuid()
    val verifyService = mock[VerifyCommandService]
    verifyService.makeVerifyCode _ expects () returning verifyCode
    verifyService.sendVerifySms _ expects (*, *) returning Future.successful(true)
    val unregister = TestServices.register(verifyService)
    assert(service.innerExecute(registerModel).await() == Right(RegisterResponseModel()))
    assertSuccessConditions(userId)
    unregister()
  }

  test("success - existing phone and user") {
    val userId = randomMock.nextUuid()
    val verifyService = mock[VerifyCommandService]
    verifyService.makeVerifyCode _ expects () returning verifyCode
    verifyService.sendVerifySms _ expects (*, *) returning Future.successful(true)
    verifyService.makeVerifyCode _ expects () returning verifyCode
    verifyService.sendVerifySms _ expects (*, *) returning Future.successful(true)
    val unregister = TestServices.register(verifyService)

    assert(service.innerExecute(registerModel).await() == Right(RegisterResponseModel()))
    assertSuccessConditions(userId)

    val phoneCooldownKey = PhoneCooldownKeyService().make(KeyId(phone))
    assert(phoneCooldownKey.del().await())

    assert(service.innerExecute(registerModel).await() == Right(RegisterResponseModel()))
    assertSuccessConditions(userId)
    unregister()
  }

  test("phone cooldown in effect") {
    val key = PhoneCooldownKeyService().make(KeyId(registerModel.phone))
    assert(key.set(1).await())
    assert(command.phoneCooldownExists(key).await() == Errors.phoneCooldown.await())
  }

  test("failed to set phone cooldown") {
    val key = mock[PhoneCooldownKey]
    key.set _ expects * returning Future.successful(false)
    assert(command.phoneCooldownSet(key).await() == Errors.phoneCooldownSetFailed.await())
  }

  test("failed to expire phone cooldown") {
    val key = mock[PhoneCooldownKey]
    key.expire _ expects * returning Future.successful(false)
    assert(command.phoneCooldownExpire(key).await() == Errors.phoneCooldownExpireFailed.await())
  }

  test("failed to create user") {
    val key = mock[PhoneKey]
    key.setUserId _ expects * returning Future.successful(false)
    assert(command.userCreate(key).await() == Errors.userSetFailed.await())
  }

  test("failed to set phone updates") {
    val key = mock[PhoneKey]
    key.setCode _ expects * returning Future.successful(false)
    assert(command.phoneSetCode(key).await() == Errors.phoneSetFailed.await())
  }

  test("failed to send sms") {
    val verifyCommandService = mock[VerifyCommandService]
    verifyCommandService.sendVerifySms _ expects (*, *) returning Future.successful(false)
    val unregister = TestServices.register(verifyCommandService)
    assert(command.smsSend(code = "code").await() == Left(Errors.smsSendFailed))
    unregister()
  }

}
