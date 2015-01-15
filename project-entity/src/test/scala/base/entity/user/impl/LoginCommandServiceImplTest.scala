/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 1:08 PM
 */

package base.entity.user.impl

import base.common.random.RandomService
import base.common.service.{ Services, TestServices }
import base.common.time.TimeService
import base.common.time.mock.TimeServiceConstantMock
import base.entity.api.ApiVersions
import base.entity.auth.context.AuthContextDataFactory
import base.entity.command.impl.CommandServiceImplTest
import base.entity.device.model.DeviceModel
import base.entity.error.ApiError
import base.entity.event.mock.EventServiceMock
import base.entity.kv.KvFactoryService
import base.entity.kv.impl.PrivateHashKeyImpl
import base.entity.kv.mock.{ KeyLoggerMock, PrivateHashKeyMock }
import base.entity.group.mock.GroupServiceMock
import base.entity.question.mock.QuestionServiceMock
import base.entity.user.UserKeyProps._
import base.entity.user.impl.LoginCommandServiceImpl._
import base.entity.user.model.{ LoginModel, LoginResponseModel }

import scala.concurrent.Future

/**
 * Responsible for testing the logic and side effects of the Impl
 *  (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class LoginCommandServiceImplTest extends CommandServiceImplTest {

  val service = new LoginCommandServiceImpl
  private val token = RandomService().uuid
  private val groupId = RandomService().uuid
  private val appVersion = "some app version"
  private val apiVersion = ApiVersions.V01
  private val locale = "some locale"
  private val deviceUuid = RandomService().uuid
  private val deviceModel = DeviceModel(deviceUuid, "Some device", "crdva", "Some platform", "Some ver", "Some name")

  private val apiError = ApiError("test error")

  private val eventMock = new EventServiceMock()
  private val groupMock = new GroupServiceMock()
  private val questionMock = new QuestionServiceMock()

  private implicit val pipeline = KvFactoryService().pipeline
  private implicit val authCtx = AuthContextDataFactory.userAuth
  private implicit val model = LoginModel(token, Option(groupId), appVersion, apiVersion, locale, deviceModel)

  override def beforeAll() {
    super.beforeAll()
    Services.register(TimeServiceConstantMock)
    Services.register(eventMock)
    Services.register(groupMock)
    Services.register(questionMock)
  }

  private def command(implicit input: LoginModel) = new service.LoginCommand(input)

  private def testSuccess(model: LoginModel, response: LoginResponseModel) {
    val userId = response.userId
    val userKey = new PrivateHashKeyImpl(s"user-$userId", KeyLoggerMock)
    val deviceKey = new PrivateHashKeyImpl(s"device-$deviceUuid", KeyLoggerMock)

    assert(deviceKey.set(TokenProp, token).await())
    assert(deviceKey.set(UserIdProp, userId).await())

    assert(service.innerExecute(model).await() == Right(response))

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
      service.execute(model)(authCtx)
    })
  }

  test("success - with group id") {
    val response = LoginResponseModel(RandomService().uuid, List(), Option(List()), Option(List()), None)
    testSuccess(model, response)
  }

  test("success - without group id") {
    val myModel = model.copy(groupId = None)
    val response = LoginResponseModel(RandomService().uuid, List(), None, None, None)
    testSuccess(myModel, response)
  }

  test("failed to get device token") {
    val mock = new PrivateHashKeyMock(getIdResult = Future.successful(None))
    val key = new DeviceKeyImpl(mock)
    assert(command.deviceGetToken(key).await() == Errors.deviceUnverified.await())
  }

  test("failed to validate token") {
    val mock = new PrivateHashKeyMock(getIdResult = Future.successful(Option(RandomService().uuid)))
    val key = new DeviceKeyImpl(mock)
    assert(command.deviceGetToken(key).await() == Errors.tokenInvalid.await())
  }

  test("failed to set device attributes") {
    val mock = new PrivateHashKeyMock(setMultiResult = Future.successful(false))
    val key = new DeviceKeyImpl(mock)
    assert(command.deviceSet(key).await() == Errors.deviceSetFailed.await())
  }

  test("failed to get device user id") {
    val mock = new PrivateHashKeyMock(getIdResult = Future.successful(None))
    val key = new DeviceKeyImpl(mock)
    assert(command.deviceGetUserId(key).await() == Errors.userIdGetFailed.await())
  }

  test("failed to get groups") {
    val unregister = TestServices.register(new GroupServiceMock(getGroupsResult = Future.successful(Left(apiError))))
    assert(command.groupsGet(RandomService().uuid).await() == Left(apiError))
    unregister()
  }

  test("failed to get group events") {
    val uuid = RandomService().uuid
    val mock = new PrivateHashKeyMock()
    val key = new UserKeyImpl(mock)
    val unregister = TestServices.register(new EventServiceMock(getEventsResult = Future.successful(Left(apiError))))
    assert(command.eventsGet(key, uuid, List(), uuid).await() == Left(apiError))
    unregister()
  }

  test("failed to get group questions") {
    val uuid = RandomService().uuid
    val mock = new PrivateHashKeyMock()
    val key = new UserKeyImpl(mock)
    val unregister = TestServices.register(new QuestionServiceMock(getResult = Future.successful(Left(apiError))))
    assert(command.eventsGet(key, uuid, List(), uuid).await() == Left(apiError))
    unregister()
  }

  test("failed to set user attributes") {
    val uuid = RandomService().uuid
    val mock = new PrivateHashKeyMock(setResult = Future.successful(false))
    val key = new UserKeyImpl(mock)
    val future = command.userGetSetLastLogin(key, uuid, List(), None, None)
    assert(future.await() == Errors.userSetFailed.await())
  }

}
