/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 2:38 PM
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
import base.entity.kv.KeyProps.{ CreatedProp, UpdatedProp }
import base.entity.kv.impl.PrivateHashKeyImpl
import base.entity.kv.mock.KeyLoggerMock
import base.entity.kv.{ KeyId, PrivateHashKey }
import base.entity.user.impl.RegisterCommandServiceImpl._
import base.entity.user.kv.UserKeyProps.{ CodeProp, UserIdProp }
import base.entity.user.kv.impl.PhoneKeyImpl
import base.entity.user.kv.{ PhoneCooldownKey, PhoneCooldownKeyService }
import base.entity.user.mock.VerifyCommandServiceMock
import base.entity.user.model.{ RegisterModel, RegisterResponseModel }
import org.scalamock.scalatest.MockFactory

import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * Responsible for testing the logic and side effects of the Impl
 *  (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class RegisterCommandServiceImplTest extends CommandServiceImplTest with MockFactory {

  val service = new RegisterCommandServiceImpl(10.minutes)

  private val phone = "555-1234"
  private val verifyCode = "verifyCode"

  private val randomMock = new RandomServiceMock()
  private val verifyMock = new VerifyCommandServiceMock(makeVerifyCodeResult = verifyCode)

  private implicit val registerModel = RegisterModel(ApiVersions.V01, phone)
  private implicit val authCtx = AuthContextDataFactory.userAuth

  override def beforeAll() {
    super.beforeAll()
    Services.register(TimeServiceConstantMock)
    Services.register(randomMock)
    Services.register(verifyMock)
  }

  private def command(implicit input: RegisterModel) = new service.RegisterCommand(input)

  private def assertSuccessConditions(userId: UUID) {
    val phoneCooldownKey = PhoneCooldownKeyService().make(KeyId(phone))
    assert(phoneCooldownKey.get().await() == Option(phoneCooldownValue))
    assert(phoneCooldownKey.ttl().await().getOrElse(-1L) > 0L)

    val phoneKey = new PrivateHashKeyImpl(s"phone-$phone", KeyLoggerMock)
    assert(phoneKey.getDateTime(CreatedProp).await().exists(_.isEqual(TimeServiceConstantMock.now)))
    assert(phoneKey.getDateTime(UpdatedProp).await().exists(_.isEqual(TimeServiceConstantMock.now)))
    assert(phoneKey.getId(UserIdProp).await().contains(userId))
    assert(phoneKey.getString(CodeProp).await().contains(verifyCode))

    val userKey = new PrivateHashKeyImpl(s"user-$userId", KeyLoggerMock)
    assert(userKey.getDateTime(CreatedProp).await().exists(_.isEqual(TimeServiceConstantMock.now)))
  }

  test("without perms") {
    assertPermException(authCtx => {
      service.execute(registerModel)(authCtx)
    })
  }

  test("success - no existing phone or user") {
    val userId = randomMock.nextUuid()
    assert(service.innerExecute(registerModel).await() == Right(RegisterResponseModel()))
    assertSuccessConditions(userId)
  }

  test("success - existing phone and user") {
    val userId = randomMock.nextUuid()
    assert(service.innerExecute(registerModel).await() == Right(RegisterResponseModel()))
    assertSuccessConditions(userId)

    val phoneCooldownKey = PhoneCooldownKeyService().make(KeyId(phone))
    assert(phoneCooldownKey.del().await())

    assert(service.innerExecute(registerModel).await() == Right(RegisterResponseModel()))
    assertSuccessConditions(userId)
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
    val key = mock[PrivateHashKey]
    (key.set(_: Map[Prop, Any])) expects * returning Future.successful(false)
    assert(command.userCreate(new PhoneKeyImpl(key)).await() == Errors.userSetFailed.await())
  }

  test("failed to set phone updates") {
    val key = mock[PrivateHashKey]
    (key.set(_: Map[Prop, Any])) expects * returning Future.successful(false)
    assert(command.phoneSetCode(new PhoneKeyImpl(key)).await() == Errors.phoneSetFailed.await())
  }

  test("failed to send sms") {
    val unregister = TestServices.register(new VerifyCommandServiceMock(sendVerifySmsResult = Future.successful(false)))
    assert(command.smsSend(code = "code").await() == Left(Errors.smsSendFailed))
    unregister()
  }

}
