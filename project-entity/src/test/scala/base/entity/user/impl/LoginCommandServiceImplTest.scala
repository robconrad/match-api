/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/27/15 7:40 PM
 */

package base.entity.user.impl

import java.util.UUID

import base.common.lib.Genders
import base.common.random.RandomService
import base.common.random.mock.RandomServiceMock
import base.common.service.{ Services, TestServices }
import base.common.time.TimeService
import base.common.time.mock.TimeServiceConstantMock
import base.entity.api.ApiVersions
import base.entity.auth.context.{ ChannelContext, ChannelContextDataFactory }
import base.entity.command.impl.CommandServiceImplTest
import base.entity.device.model.DeviceModel
import base.entity.error.ApiError
import base.entity.facebook.{ FacebookInfo, FacebookService }
import base.entity.group.{ GroupListenerService, GroupEventsService }
import base.entity.kv.Key._
import base.entity.question.QuestionService
import base.entity.question.model.AnswerModel
import base.entity.user.UserService
import base.entity.user.impl.LoginCommandServiceImpl._
import base.entity.user.kv._
import base.entity.user.model.{ LoginModel, LoginResponseModel }

import scala.concurrent.Future

/**
 * Responsible for testing the logic and side effects of the Impl
 *  (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class LoginCommandServiceImplTest extends CommandServiceImplTest {

  val service = new LoginCommandServiceImpl
  private val fbToken = RandomService().uuid.toString
  private val fbId = RandomService().uuid.toString
  private val groupId = RandomService().uuid
  private val appVersion = "some app version"
  private val apiVersion = ApiVersions.V01
  private val locale = "some locale"
  private val deviceUuid = RandomService().uuid
  private val deviceModel = DeviceModel(deviceUuid, "Some device", "crdva", "Some platform", "Some ver", "Some name")

  private val apiError = ApiError("test error")
  private val randomMock = new RandomServiceMock()

  private implicit val channelCtx = ChannelContextDataFactory.userAuth
  private implicit val model = LoginModel(fbToken, Option(groupId), appVersion, apiVersion, locale, deviceModel)

  override def beforeAll() {
    super.beforeAll()
    Services.register(TimeServiceConstantMock)
    Services.register(randomMock)
    registerQuestionMock()
  }

  private def registerQuestionMock() {
    val questionMock = mock[QuestionService]
    (questionMock.answer(_: AnswerModel)(_: Pipeline, _: ChannelContext)) expects
      (*, *, *) returning Future.successful(Right(List())) anyNumberOfTimes ()
    (questionMock.getQuestions(_: UUID)(_: Pipeline, _: ChannelContext)) expects
      (*, *, *) returning Future.successful(Right(List())) anyNumberOfTimes ()
    Services.register(questionMock)
  }

  private def command(implicit input: LoginModel) = new service.LoginCommand(input)

  private def testSuccess(userId: UUID, model: LoginModel, response: LoginResponseModel) {
    val userKey = UserKeyService().make(userId)
    val deviceKey = DeviceKeyService().make(deviceUuid)
    val name = "bob"
    val gender = "male"

    val facebook = mock[FacebookService]
    val groupListener = mock[GroupListenerService]
    val unregister = TestServices.register(groupListener, facebook)
    (groupListener.register(_: UUID, _: Set[UUID])(_: ChannelContext)) expects
      (*, *, *) returning Future.successful(Unit)
    (facebook.getInfo(_: String)(_: ChannelContext)) expects
      (*, *) returning Future.successful(Option(FacebookInfo(fbId, name, gender, locale)))

    assert(service.innerExecute(model).await() == Right(response))

    assert(userKey.getLastLogin.await().exists(_.isEqual(TimeService().now)))
    assert(userKey.getFacebookId.await().contains(fbId))
    assert(userKey.getNameAndGender.await() == (Option(name), Option(Genders.withName(gender))))
    assert(userKey.getLocale.await().contains(locale))
    assert(userKey.getUpdated.await().exists(_.isEqual(TimeServiceConstantMock.now)))
    assert(deviceKey.getAppVersion.await().contains(appVersion))
    assert(deviceKey.getLocale.await().contains(locale))
    assert(deviceKey.getModel.await().contains(deviceModel.model))
    assert(deviceKey.getCordova.await().contains(deviceModel.cordova))
    assert(deviceKey.getPlatform.await().contains(deviceModel.platform))
    assert(deviceKey.getVersion.await().contains(deviceModel.version))

    unregister()
  }

  test("without perms") {
    assertPermException(channelCtx => {
      service.execute(model)(channelCtx)
    })
  }

  test("success - new user") {
    val userId = randomMock.nextUuid()
    val myModel = model.copy(groupId = None)
    val response = LoginResponseModel(userId, List(), None, None, None)
    testSuccess(userId, myModel, response)
  }

  test("success - existing user - without group id") {
    val userId = RandomService().uuid
    assert(FacebookUserKeyService().make(fbId).set(userId).await())
    val myModel = model.copy(groupId = None)
    val response = LoginResponseModel(userId, List(), None, None, None)
    testSuccess(userId, myModel, response)
  }

  test("success - existing user - with group id") {
    registerQuestionMock()
    val userId = RandomService().uuid
    assert(FacebookUserKeyService().make(fbId).set(userId).await())
    val response = LoginResponseModel(userId, List(), Option(List()), Option(List()), None)
    testSuccess(userId, model, response)
  }

  test("failed to authenticate facebook token") {
    val facebook = mock[FacebookService]
    (facebook.getInfo(_: String)(_: ChannelContext)) expects (*, *) returning Future.successful(None)
    val unregister = TestServices.register(facebook)
    assert(command.facebookInfoGet().await() == Errors.tokenInvalid.await())
    unregister()
  }

  test("failed to set facebook info to user") {
    val userId = RandomService().uuid
    val fbInfo = FacebookInfo("", "", "", "")
    val key = mock[UserKey]
    key.setFacebookInfo _ expects * returning Future.successful(false)
    assert(command.userSet(key, userId, fbInfo).await() == Errors.userSetFailed.await())
  }

  test("failed to set device attributes") {
    val userId = RandomService().uuid
    val key = mock[DeviceKey]
    key.set _ expects (*, *, *, *, *, *) returning Future.successful(false)
    assert(command.deviceSet(key, userId).await() == Errors.deviceSetFailed.await())
  }

  test("failed to get groups") {
    val userService = mock[UserService]
    (userService.getGroups(_: UUID)(_: Pipeline, _: ChannelContext)) expects
      (*, *, *) returning Future.successful(Left(apiError))
    val unregister = TestServices.register(userService)
    assert(command.groupsGet(RandomService().uuid).await() == Left(apiError))
    unregister()
  }

  test("failed to get group events") {
    val uuid = RandomService().uuid
    val key = mock[UserKey]
    val result = Future.successful(Left(apiError))
    val groupEventsService = mock[GroupEventsService]
    (groupEventsService.getEvents(_: UUID)(_: Pipeline)) expects (*, *) returning result
    val unregister = TestServices.register(groupEventsService)
    assert(command.eventsGet(key, uuid, List(), uuid).await() == Left(apiError))
    unregister()
  }

  test("failed to get group questions") {
    val uuid = RandomService().uuid
    val key = mock[UserKey]
    val questionMock = mock[QuestionService]
    (questionMock.getQuestions(_: UUID)(_: Pipeline, _: ChannelContext)) expects
      (*, *, *) returning Future.successful(Left(apiError))
    val unregister = TestServices.register(questionMock)
    assert(command.eventsGet(key, uuid, List(), uuid).await() == Left(apiError))
    unregister()
  }

  test("failed to set user attributes") {
    val uuid = RandomService().uuid
    val key = mock[UserKey]
    key.getLastLogin _ expects () returning Future.successful(None)
    key.setLastLogin _ expects * returning Future.successful(false)
    val future = command.userGetSetLastLogin(key, uuid, List(), None, None)
    assert(future.await() == Errors.userSetFailed.await())
  }

}
