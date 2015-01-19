/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 4:15 PM
 */

package base.entity.user.impl

import java.util.UUID

import base.common.random.RandomService
import base.common.service.{ Services, TestServices }
import base.common.time.TimeService
import base.common.time.mock.TimeServiceConstantMock
import base.entity.api.ApiVersions
import base.entity.auth.context.{ AuthContext, AuthContextDataFactory }
import base.entity.command.impl.CommandServiceImplTest
import base.entity.device.model.DeviceModel
import base.entity.error.ApiError
import base.entity.group.{ GroupEventsService, UserService }
import base.entity.kv.Key._
import base.entity.kv.PrivateHashKey
import base.entity.kv.impl.PrivateHashKeyImpl
import base.entity.kv.mock.KeyLoggerMock
import base.entity.question.QuestionService
import base.entity.question.model.AnswerModel
import base.entity.user.impl.LoginCommandServiceImpl._
import base.entity.user.kv.UserKeyProps._
import base.entity.user.kv.impl.{ DeviceKeyImpl, UserKeyImpl }
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

  private implicit val authCtx = AuthContextDataFactory.userAuth
  private implicit val model = LoginModel(token, Option(groupId), appVersion, apiVersion, locale, deviceModel)

  override def beforeAll() {
    super.beforeAll()
    Services.register(TimeServiceConstantMock)
    registerQuestionMock()
  }

  private def registerQuestionMock() {
    val questionMock = mock[QuestionService]
    (questionMock.answer(_: AnswerModel)(_: Pipeline, _: AuthContext)) expects
      (*, *, *) returning Future.successful(Right(List())) anyNumberOfTimes ()
    (questionMock.getQuestions(_: UUID)(_: Pipeline, _: AuthContext)) expects
      (*, *, *) returning Future.successful(Right(List())) anyNumberOfTimes ()
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
    registerQuestionMock()
    val response = LoginResponseModel(RandomService().uuid, List(), Option(List()), Option(List()), None)
    testSuccess(model, response)
  }

  test("success - without group id") {
    val myModel = model.copy(groupId = None)
    val response = LoginResponseModel(RandomService().uuid, List(), None, None, None)
    testSuccess(myModel, response)
  }

  test("failed to get device token") {
    val key = mock[PrivateHashKey]
    key.getId _ expects * returning Future.successful(None)
    assert(command.deviceGetToken(new DeviceKeyImpl(key)).await() == Errors.deviceUnverified.await())
  }

  test("failed to validate token") {
    val key = mock[PrivateHashKey]
    key.getId _ expects * returning Future.successful(Option(RandomService().uuid))
    assert(command.deviceGetToken(new DeviceKeyImpl(key)).await() == Errors.tokenInvalid.await())
  }

  test("failed to set device attributes") {
    val key = mock[PrivateHashKey]
    (key.set(_: Map[Prop, Any])) expects * returning Future.successful(false)
    assert(command.deviceSet(new DeviceKeyImpl(key)).await() == Errors.deviceSetFailed.await())
  }

  test("failed to get device user id") {
    val key = mock[PrivateHashKey]
    key.getId _ expects * returning Future.successful(None)
    assert(command.deviceGetUserId(new DeviceKeyImpl(key)).await() == Errors.userIdGetFailed.await())
  }

  test("failed to get groups") {
    val userService = mock[UserService]
    (userService.getGroups(_: UUID)(_: Pipeline, _: AuthContext)) expects
      (*, *, *) returning Future.successful(Left(apiError))
    val unregister = TestServices.register(userService)
    assert(command.groupsGet(RandomService().uuid).await() == Left(apiError))
    unregister()
  }

  test("failed to get group events") {
    val uuid = RandomService().uuid
    val key = new UserKeyImpl(mock[PrivateHashKey])
    val result = Future.successful(Left(apiError))
    val groupEventsService = mock[GroupEventsService]
    (groupEventsService.getEvents(_: UUID)(_: Pipeline)) expects (*, *) returning result
    val unregister = TestServices.register(groupEventsService)
    assert(command.eventsGet(key, uuid, List(), uuid).await() == Left(apiError))
    unregister()
  }

  test("failed to get group questions") {
    val uuid = RandomService().uuid
    val key = new UserKeyImpl(mock[PrivateHashKey])
    val questionMock = mock[QuestionService]
    (questionMock.getQuestions(_: UUID)(_: Pipeline, _: AuthContext)) expects
      (*, *, *) returning Future.successful(Left(apiError))
    val unregister = TestServices.register(questionMock)
    assert(command.eventsGet(key, uuid, List(), uuid).await() == Left(apiError))
    unregister()
  }

  test("failed to set user attributes") {
    val uuid = RandomService().uuid
    val key = mock[PrivateHashKey]
    key.getDateTime _ expects * returning Future.successful(None)
    (key.set(_: Prop, _: Any)) expects (*, *) returning Future.successful(false)
    val future = command.userGetSetLastLogin(new UserKeyImpl(key), uuid, List(), None, None)
    assert(future.await() == Errors.userSetFailed.await())
  }

}
