/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 6:18 PM
 */

package base.entity.user.impl

import base.common.lib.Genders
import base.common.random.RandomService
import base.common.random.mock.RandomServiceMock
import base.common.service.{ Services, TestServices }
import base.common.time.mock.TimeServiceConstantMock
import base.entity.api.ApiVersions
import base.entity.auth.context.AuthContextDataFactory
import base.entity.kv.Key.Prop
import base.entity.kv.KeyProps.{ CreatedProp, UpdatedProp }
import base.entity.kv.mock.HashKeyMock
import base.entity.kv.{ KeyId, KvService, KvTest }
import base.entity.service.EntityServiceTest
import base.entity.sms.mock.SmsServiceMock
import base.entity.user.UserKeyFactories.{ DeviceKeyFactory, PhoneKeyFactory, UserKeyFactory }
import base.entity.user.UserKeyProps._
import base.entity.user.impl.VerifyServiceImpl._
import base.entity.user.model.{ VerifyModel, VerifyResponseModel }

import scala.concurrent.Future

/**
 * Responsible for testing the logic and side effects of the Impl
 * (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class VerifyServiceImplTest extends EntityServiceTest with KvTest {

  val codeLength = 6

  val service = new VerifyServiceImpl(codeLength, "body %s")

  private val code = "Some Code"
  private val phone = "555-1234"
  private val name = "bob"
  private val gender = Genders.male
  private val device = RandomService().uuid

  private val randomMock = new RandomServiceMock()

  private implicit val pipeline = KvService().pipeline
  private implicit val authCtx = AuthContextDataFactory.userAuth
  private implicit val model = VerifyModel(ApiVersions.V01, Option(name), Option(gender), phone, device, code)

  override def beforeAll() {
    super.beforeAll()
    Services.register(new SmsServiceMock(result = true))
    Services.register(TimeServiceConstantMock)
    Services.register(randomMock)
  }

  test("without perms") {
    assertPermException(authCtx => {
      service.verify(model)(authCtx)
    })
  }

  test("success") {
    val userId = RandomService().uuid
    val token = randomMock.nextUuid()
    val props = Map[Prop, Any](
      CodeProp -> code,
      UserIdProp -> userId)
    assert(PhoneKeyFactory().make(KeyId(phone)).set(props).await())
    assert(service.verify(model).await() == Right(VerifyResponseModel(token)))

    val userKey = UserKeyFactory().make(KeyId(userId))
    assert(userKey.getString(NameProp).await().contains(name))
    assert(userKey.getString(GenderProp).await().contains(gender.toString))
    assert(userKey.getDateTime(UpdatedProp).await().exists(_.isEqual(TimeServiceConstantMock.now)))

    val deviceKey = DeviceKeyFactory().make(KeyId(device))
    assert(deviceKey.getDateTime(CreatedProp).await().exists(_.isEqual(TimeServiceConstantMock.now)))
    assert(deviceKey.getDateTime(UpdatedProp).await().exists(_.isEqual(TimeServiceConstantMock.now)))
    assert(deviceKey.getId(TokenProp).await().contains(token))
  }

  test("failed to get phone verification code") {
    assert(service.phoneKeyGet().await() == externalErrorNoCodeResponse.await())
  }

  test("failed to validate phone verification code") {
    val key = new HashKeyMock()
    val code = model.code + "munge"
    assert(service.phoneKeyVerify(key, code).await() == externalErrorCodeValidationResponse.await())
  }

  test("failed to get userId") {
    val key = new HashKeyMock(getIdResult = Future.successful(None))
    assert(service.userIdGet(key).await() == internalErrorNoUserIdResponse.await())
  }

  test("failed to provide name on first verify") {
    val myModel = model.copy(name = None)
    val key = new HashKeyMock(getMultiResult = Future.successful(Map(GenderProp -> Option(gender.toString))))
    assert(service.userKeyGet(key)(myModel, p).await() == externalErrorRequiredParamsResponse.await())
  }

  test("failed to provide gender on first verify") {
    val myModel = model.copy(gender = None)
    val key = new HashKeyMock(getMultiResult = Future.successful(Map(NameProp -> Option(name))))
    assert(service.userKeyGet(key)(myModel, p).await() == externalErrorRequiredParamsResponse.await())
  }

  test("failed to set user attributes") {
    val key = new HashKeyMock(setMultiResult = Future.successful(false))
    assert(service.userKeySet(key, name, gender).await() == internalErrorSetUserFailedResponse.await())
  }

  test("failed to set device attributes") {
    val key = new HashKeyMock(setMultiResult = Future.successful(false))
    assert(service.deviceKeySet(key).await() == internalErrorSetDeviceFailedResponse.await())
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
