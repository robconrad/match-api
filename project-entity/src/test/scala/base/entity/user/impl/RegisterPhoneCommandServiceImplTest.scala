/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 9:41 AM
 */

package base.entity.user.impl

import java.util.UUID

import base.common.random.RandomService
import base.common.random.mock.RandomServiceMock
import base.common.service.{ Services, TestServices }
import base.common.time.mock.TimeServiceConstantMock
import base.entity.auth.context.ChannelContextDataFactory
import base.entity.command.impl.CommandServiceImplTest
import base.entity.user.VerifyPhoneCommandService
import base.entity.user.impl.RegisterPhoneCommandServiceImpl._
import base.entity.user.kv._
import base.entity.user.model.{ RegisterPhoneModel, RegisterPhoneResponseModel }

import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * Responsible for testing the logic and side effects of the Impl
 *  (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class RegisterPhoneCommandServiceImplTest extends CommandServiceImplTest {

  val service = new RegisterPhoneCommandServiceImpl(10.minutes)

  private val phone = "555-1234"
  private val verifyCode = "verifyCode"

  private val randomMock = new RandomServiceMock()

  private implicit val model = RegisterPhoneModel(phone)
  private implicit val channelCtx = ChannelContextDataFactory.userAuth

  override def beforeAll() {
    super.beforeAll()
    Services.register(TimeServiceConstantMock)
    Services.register(randomMock)
  }

  private def command(implicit input: RegisterPhoneModel) = new service.RegisterCommand(input)

  private def assertSuccessConditions(userId: UUID) {
    val phoneCooldownKey = PhoneCooldownKeyService().make(phone)
    assert(phoneCooldownKey.get().await() == Option(phoneCooldownValue))
    assert(phoneCooldownKey.ttl().await().getOrElse(-1L) > 0L)

    val phoneKey = PhoneKeyService().make(phone)
    assert(phoneKey.getCreated.await().exists(_.isEqual(TimeServiceConstantMock.now)))
    assert(phoneKey.getUpdated.await().exists(_.isEqual(TimeServiceConstantMock.now)))
    assert(phoneKey.getUserId.await().contains(userId))
    assert(phoneKey.getCode.await().contains(verifyCode))

    val userKey = UserKeyService().make(userId)
    assert(userKey.getCreated.await().exists(_.isEqual(TimeServiceConstantMock.now)))
  }

  test("without perms") {
    assertPermException(channelCtx => {
      service.execute(model)(channelCtx)
    })
  }

  test("success - no existing phone or user") {
    val userId = randomMock.nextUuid()
    val verifyService = mock[VerifyPhoneCommandService]
    verifyService.makeVerifyCode _ expects () returning verifyCode
    verifyService.sendVerifySms _ expects (*, *) returning Future.successful(true)
    val unregister = TestServices.register(verifyService)
    assert(service.innerExecute(model).await() == Right(RegisterPhoneResponseModel(phone)))
    assertSuccessConditions(userId)
    unregister()
  }

  test("success - existing phone and user") {
    val userId = randomMock.nextUuid()
    val verifyService = mock[VerifyPhoneCommandService]
    verifyService.makeVerifyCode _ expects () returning verifyCode
    verifyService.sendVerifySms _ expects (*, *) returning Future.successful(true)
    verifyService.makeVerifyCode _ expects () returning verifyCode
    verifyService.sendVerifySms _ expects (*, *) returning Future.successful(true)
    val unregister = TestServices.register(verifyService)

    assert(service.innerExecute(model).await() == Right(RegisterPhoneResponseModel(phone)))
    assertSuccessConditions(userId)

    val phoneCooldownKey = PhoneCooldownKeyService().make(phone)
    assert(phoneCooldownKey.del().await())

    assert(service.innerExecute(model).await() == Right(RegisterPhoneResponseModel(phone)))
    assertSuccessConditions(userId)
    unregister()
  }

  test("phone cooldown in effect") {
    val key = PhoneCooldownKeyService().make(model.phone)
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
    val userId = RandomService().uuid
    val userKey = mock[UserKey]
    val phoneKey = mock[PhoneKey]
    userKey.create _ expects () returning Future.successful(false)
    assert(command.userCreate(userId, userKey, phoneKey).await() == Errors.userSetFailed.await())
  }

  test("failed to set phone userId on create") {
    val userId = RandomService().uuid
    val userKey = mock[UserKey]
    val phoneKey = mock[PhoneKey]
    userKey.create _ expects () returning Future.successful(true)
    phoneKey.setUserId _ expects * returning Future.successful(false)
    assert(command.userCreate(userId, userKey, phoneKey).await() == Errors.userSetFailed.await())
  }

  test("failed to set phone updates") {
    val key = mock[PhoneKey]
    key.setCode _ expects * returning Future.successful(false)
    assert(command.phoneSetCode(key).await() == Errors.phoneSetFailed.await())
  }

  test("failed to send sms") {
    val verifyCommandService = mock[VerifyPhoneCommandService]
    verifyCommandService.sendVerifySms _ expects (*, *) returning Future.successful(false)
    val unregister = TestServices.register(verifyCommandService)
    assert(command.smsSend(code = "code").await() == Left(Errors.smsSendFailed))
    unregister()
  }

}
