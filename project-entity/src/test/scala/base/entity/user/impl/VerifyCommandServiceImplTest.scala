/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 12:35 PM
 */

package base.entity.user.impl

import base.common.lib.Genders
import base.common.random.RandomService
import base.common.random.mock.RandomServiceMock
import base.common.service.{ Services, TestServices }
import base.common.time.mock.TimeServiceConstantMock
import base.entity.api.ApiVersions
import base.entity.auth.context.AuthContextDataFactory
import base.entity.command.impl.CommandServiceImplTest
import base.entity.kv.Key.Prop
import base.entity.kv.KeyProps.{ CreatedProp, UpdatedProp }
import base.entity.kv.KvFactoryService
import base.entity.kv.impl.PrivateHashKeyImpl
import base.entity.kv.mock.{ KeyLoggerMock, PrivateHashKeyMock }
import base.entity.sms.mock.SmsServiceMock
import base.entity.user.UserKeyProps._
import base.entity.user.impl.VerifyCommandServiceImpl._
import base.entity.user.model.{ VerifyModel, VerifyResponseModel }

import scala.concurrent.Future

/**
 * Responsible for testing the logic and side effects of the Impl
 * (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class VerifyCommandServiceImplTest extends CommandServiceImplTest {

  val codeLength = 6

  val service = new VerifyCommandServiceImpl(codeLength, "body %s")

  private val code = "Some Code"
  private val phone = "555-1234"
  private val name = "bob"
  private val gender = Genders.male
  private val device = RandomService().uuid
  private val userId = RandomService().uuid

  private val randomMock = new RandomServiceMock()

  private implicit val pipeline = KvFactoryService().pipeline
  private implicit val authCtx = AuthContextDataFactory.userAuth
  private implicit val model = VerifyModel(ApiVersions.V01, Option(name), Option(gender), phone, device, code)

  override def beforeAll() {
    super.beforeAll()
    Services.register(new SmsServiceMock(result = true))
    Services.register(TimeServiceConstantMock)
    Services.register(randomMock)
  }

  private def command(implicit input: VerifyModel) = new service.VerifyCommand(input)

  test("without perms") {
    assertPermException(authCtx => {
      service.execute(model)(authCtx)
    })
  }

  test("success") {
    val userId = RandomService().uuid
    val token = randomMock.nextUuid()
    val props = Map[Prop, Any](
      CodeProp -> code,
      UserIdProp -> userId)
    val phoneKey = new PrivateHashKeyImpl(s"phone-$phone", KeyLoggerMock)
    assert(phoneKey.set(props).await())
    assert(service.innerExecute(model).await() == Right(VerifyResponseModel(token)))

    val userKey = new PrivateHashKeyImpl(s"user-$userId", KeyLoggerMock)
    assert(userKey.getString(NameProp).await().contains(name))
    assert(userKey.getString(GenderProp).await().contains(gender.toString))
    assert(userKey.getDateTime(UpdatedProp).await().exists(_.isEqual(TimeServiceConstantMock.now)))

    val deviceKey = new PrivateHashKeyImpl(s"device-$device", KeyLoggerMock)
    assert(deviceKey.getDateTime(CreatedProp).await().exists(_.isEqual(TimeServiceConstantMock.now)))
    assert(deviceKey.getDateTime(UpdatedProp).await().exists(_.isEqual(TimeServiceConstantMock.now)))
    assert(deviceKey.getId(TokenProp).await().contains(token))
    assert(deviceKey.getId(UserIdProp).await().contains(userId))
  }

  test("failed to get phone verification code") {
    val key = new PhoneKeyImpl(new PrivateHashKeyMock())
    assert(command.phoneGetCode(key).await() == Errors.codeMissing.await())
  }

  test("failed to validate phone verification code") {
    val key = new PhoneKeyImpl(new PrivateHashKeyMock())
    val code = model.code + "munge"
    assert(command.phoneVerify(key, code).await() == Errors.codeValidation.await())
  }

  test("failed to get userId") {
    val key = new PhoneKeyImpl(new PrivateHashKeyMock(getIdResult = Future.successful(None)))
    assert(command.phoneGetUserId(key).await() == Errors.userIdMissing.await())
  }

  test("failed to provide name on first verify") {
    val myModel = model.copy(name = None)
    val result = Future.successful(Map[Prop, Option[String]](GenderProp -> Option(gender.toString)))
    val mock = new PrivateHashKeyMock(getMultiResult = result)
    val key = new UserKeyImpl(mock)
    assert(command(myModel).userGet(userId, key).await() == Errors.paramsMissing.await())
  }

  test("failed to provide gender on first verify") {
    val myModel = model.copy(gender = None)
    val mock = new PrivateHashKeyMock(getMultiResult = Future.successful(Map(NameProp -> Option(name))))
    val key = new UserKeyImpl(mock)
    assert(command(myModel).userGet(userId, key).await() == Errors.paramsMissing.await())
  }

  test("failed to set user attributes") {
    val mock = new PrivateHashKeyMock(setMultiResult = Future.successful(false))
    val key = new UserKeyImpl(mock)
    assert(command.userSet(userId, key, name, gender).await() == Errors.userSetFailed.await())
  }

  test("failed to set device attributes") {
    val mock = new PrivateHashKeyMock(setMultiResult = Future.successful(false))
    val key = new DeviceKeyImpl(mock)
    assert(command.deviceSet(userId, key).await() == Errors.deviceSetFailed.await())
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
