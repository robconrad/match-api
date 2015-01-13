/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/12/15 9:19 PM
 */

package base.entity.user.impl

import base.common.random.RandomService
import base.common.service.{ TestServices, Services }
import base.common.time.TimeService
import base.common.time.mock.{ TimeServiceConstantMock, TimeServiceMonotonicMock }
import base.entity.api.ApiVersions
import base.entity.auth.context.AuthContextDataFactory
import base.entity.device.model.DeviceModel
import base.entity.error.ApiError
import base.entity.event.mock.EventServiceMock
import base.entity.kv.mock.HashKeyMock
import base.entity.kv.{ KeyId, KvService, KvTest }
import base.entity.pair.mock.PairServiceMock
import base.entity.question.mock.QuestionServiceMock
import base.entity.service.EntityServiceTest
import base.entity.user.UserKeyFactories.{ UserKeyFactory, DeviceKeyFactory }
import base.entity.user.UserKeyProps._
import base.entity.user.impl.LoginServiceImpl._
import base.entity.user.model.{ LoginModel, LoginResponseModel }

import scala.concurrent.Future

/**
 * Responsible for testing the logic and side effects of the Impl
 *  (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class LoginServiceImplTest extends EntityServiceTest with KvTest {

  val service = new LoginServiceImpl
  private val token = RandomService().uuid
  private val pairId = RandomService().uuid
  private val appVersion = "some app version"
  private val apiVersion = ApiVersions.V01
  private val locale = "some locale"
  private val deviceUuid = RandomService().uuid
  private val deviceModel = DeviceModel(deviceUuid, "Some device", "crdva", "Some platform", "Some ver", "Some name")

  private val apiError = ApiError("test error")

  private val eventMock = new EventServiceMock()
  private val pairMock = new PairServiceMock()
  private val questionMock = new QuestionServiceMock()

  private implicit val pipeline = KvService().pipeline
  private implicit val authCtx = AuthContextDataFactory.userAuth
  private implicit val model = LoginModel(token, Option(pairId), appVersion, apiVersion, locale, deviceModel)

  override def beforeAll() {
    super.beforeAll()
    Services.register(TimeServiceConstantMock)
    Services.register(eventMock)
    Services.register(pairMock)
    Services.register(questionMock)
  }

  private def testSuccess(model: LoginModel, response: LoginResponseModel) {
    val userId = response.userId
    val userKey = UserKeyFactory().make(KeyId(userId))
    val deviceKey = DeviceKeyFactory().make(KeyId(deviceUuid))

    assert(deviceKey.set(TokenProp, token).await())
    assert(deviceKey.set(UserIdProp, userId).await())

    assert(service.login(model).await() == Right(response))

    assert(userKey.getDateTime(LastLoginProp).await().exists(_.isEqual(TimeService().now)))
    assert(deviceKey.getString(AppVersionProp).await().contains(appVersion))
    assert(deviceKey.getString(LocaleProp).await().contains(locale))
    assert(deviceKey.getString(ModelProp).await().contains(deviceModel.model))
    assert(deviceKey.getString(CordovaProp).await().contains(deviceModel.cordova))
    assert(deviceKey.getString(PlatformProp).await().contains(deviceModel.platform))
    assert(deviceKey.getString(VersionProp).await().contains(deviceModel.version))
  }

  test("without perms") {
    assertPermException(authCtx => {
      service.login(model)(authCtx)
    })
  }

  test("success - with pair id") {
    val response = LoginResponseModel(RandomService().uuid, List(), Option(List()), Option(List()), None)
    testSuccess(model, response)
  }

  test("success - without pair id") {
    val myModel = model.copy(pairId = None)
    val response = LoginResponseModel(RandomService().uuid, List(), None, None, None)
    testSuccess(myModel, response)
  }

  test("failed to get device token") {
    val deviceKey = new HashKeyMock(getIdResult = Future.successful(None))
    assert(service.deviceKeyGet(deviceKey).await() == externalErrorDeviceNotVerifiedResponse.await())
  }

  test("failed to validate token") {
    val deviceKey = new HashKeyMock(getIdResult = Future.successful(Option(RandomService().uuid)))
    assert(service.deviceKeyGet(deviceKey).await() == externalErrorTokenNotValidResponse.await())
  }

  test("failed to set device attributes") {
    val deviceKey = new HashKeyMock(setMultiResult = Future.successful(false))
    assert(service.deviceKeySet(deviceKey).await() == internalErrorDeviceSetFailedResponse.await())
  }

  test("failed to get device user id") {
    val deviceKey = new HashKeyMock(getIdResult = Future.successful(None))
    assert(service.userKeyGet(deviceKey).await() == internalErrorUserIdGetFailedResponse.await())
  }

  test("failed to get pairs") {
    val unregister = TestServices.register(new PairServiceMock(getPairsResult = Future.successful(Left(apiError))))
    assert(service.getPairs(RandomService().uuid).await() == Left(apiError))
    unregister()
  }

  test("failed to get pair events") {
    val uuid = RandomService().uuid
    val userKey = new HashKeyMock()
    val unregister = TestServices.register(new EventServiceMock(getEventsResult = Future.successful(Left(apiError))))
    assert(service.getPairEvents(userKey, uuid, List(), uuid).await() == Left(apiError))
    unregister()
  }

  test("failed to get pair questions") {
    val uuid = RandomService().uuid
    val userKey = new HashKeyMock()
    val unregister = TestServices.register(new QuestionServiceMock(getResult = Future.successful(Left(apiError))))
    assert(service.getPairEvents(userKey, uuid, List(), uuid).await() == Left(apiError))
    unregister()
  }

  test("failed to set user attributes") {
    val uuid = RandomService().uuid
    val userKey = new HashKeyMock(setResult = Future.successful(false))
    val future = service.userKeyGetLogin(userKey, uuid, List(), None, None)
    assert(future.await() == internalErrorUserSetFailedResponse.await())
  }

}
