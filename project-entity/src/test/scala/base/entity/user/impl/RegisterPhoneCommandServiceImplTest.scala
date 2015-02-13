/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/12/15 8:53 PM
 */

package base.entity.user.impl

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

  private val intMin = 100000
  private val intMax = 1000000
  private val randomMock = new RandomServiceMock(intMin = intMin, intMax = intMax)

  private implicit val model = RegisterPhoneModel(phone)
  private implicit val channelCtx = ChannelContextDataFactory.userAuth

  override def beforeAll() {
    super.beforeAll()
    Services.register(TimeServiceConstantMock)
    Services.register(randomMock)
  }

  private def command(implicit input: RegisterPhoneModel) = new service.RegisterCommand(input)

  private def assertSuccessConditions() {
    val phoneCooldownKey = make[PhoneCooldownKey](phone)
    assert(phoneCooldownKey.get.await().contains(true))
    phoneCooldownKey.ttl.await() match {
      case Left(b)    => fail()
      case Right(ttl) => assert(ttl > 0)
    }

    val userKey = make[UserKey](authCtx.userId)
    val phoneAttributes = userKey.getPhoneAttributes.await()
    assert(userKey.getCreated.await().exists(_.isEqual(TimeServiceConstantMock.now)))
    assert(phoneAttributes.exists(_.phone == phone))
    assert(phoneAttributes.exists(_.code == verifyCode))
    assert(phoneAttributes.exists(!_.verified))
  }

  test("without perms") {
    assertPermException(channelCtx => {
      service.execute(model)(channelCtx)
    })
  }

  test("success - no existing phone or user") {
    val verifyService = mock[VerifyPhoneCommandService]
    verifyService.makeVerifyCode _ expects () returning verifyCode
    verifyService.sendVerifySms _ expects (*, *) returning Future.successful(true)
    val unregister = TestServices.register(verifyService)
    assert(service.innerExecute(model).await() == Right(RegisterPhoneResponseModel(phone)))
    assertSuccessConditions()
    unregister()
  }

  test("success - existing phone and user") {
    val verifyService = mock[VerifyPhoneCommandService]
    verifyService.makeVerifyCode _ expects () returning verifyCode
    verifyService.sendVerifySms _ expects (*, *) returning Future.successful(true)
    verifyService.makeVerifyCode _ expects () returning verifyCode
    verifyService.sendVerifySms _ expects (*, *) returning Future.successful(true)
    val unregister = TestServices.register(verifyService)

    assert(service.innerExecute(model).await() == Right(RegisterPhoneResponseModel(phone)))
    assertSuccessConditions()

    val phoneCooldownKey = make[PhoneCooldownKey](phone)
    assert(phoneCooldownKey.del().await())

    assert(service.innerExecute(model).await() == Right(RegisterPhoneResponseModel(phone)))
    assertSuccessConditions()
    unregister()
  }

  test("phone cooldown in effect") {
    val key = make[PhoneCooldownKey](model.phone)
    assert(key.set(value = true).await())
    assert(command.phoneCooldownExists(key).await() == Errors.phoneCooldown.await())
  }

  test("failed to set phone cooldown") {
    val key = mock[PhoneCooldownKey]
    key.set _ expects (*, *, *) returning Future.successful(false)
    assert(command.phoneCooldownSet(key).await() == Errors.phoneCooldownSetFailed.await())
  }

  test("failed to expire phone cooldown") {
    val key = mock[PhoneCooldownKey]
    key.expire _ expects * returning Future.successful(false)
    assert(command.phoneCooldownExpire(key).await() == Errors.phoneCooldownExpireFailed.await())
  }

  test("failed to send sms") {
    val verifyCommandService = mock[VerifyPhoneCommandService]
    verifyCommandService.sendVerifySms _ expects (*, *) returning Future.successful(false)
    val unregister = TestServices.register(verifyCommandService)
    assert(command.smsSend(code = "code").await() == Left(Errors.smsSendFailed))
    unregister()
  }

}
