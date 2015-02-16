/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 5:27 PM
 */

package base.entity.user.impl

import java.util.UUID

import base.common.random.RandomService
import base.common.random.mock.RandomServiceMock
import base.common.service.{ Services, TestServices }
import base.common.test.TestExceptions.TestRuntimeException
import base.common.time.TimeService
import base.common.time.mock.TimeServiceConstantMock
import base.entity.api.ApiVersions
import base.entity.auth.context.{ ChannelContext, ChannelContextDataFactory }
import base.entity.command.impl.CommandServiceImplTest
import base.entity.device.model.DeviceModel
import base.entity.error.ApiErrorService
import base.entity.facebook.{ FacebookInfo, FacebookService }
import base.entity.group.kv.GroupUsersKey
import base.entity.group.{ GroupEventsService, GroupListenerService }
import base.entity.question.QuestionService
import base.entity.question.model.AnswerModel
import base.entity.user.UserService
import base.entity.user.impl.LoginCommandServiceImpl._
import base.entity.user.kv._
import base.entity.user.model.impl.{ LoginResponseModelBuilder, LoginResponseModelImpl }
import base.entity.user.model.{ LoginModel, LoginResponseModel, UserModel }

import scala.concurrent.Future

/**
 * Responsible for testing the logic and side effects of the Impl
 *  (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class LoginCommandServiceImplTest extends CommandServiceImplTest {

  val service = new LoginCommandServiceImpl
  private val name = "bob"
  private val fbToken = RandomService().uuid.toString
  private val fbId = RandomService().uuid.toString
  private val pictureUrl = FacebookService().getPictureUrl(fbId)
  private val groupId = RandomService().uuid
  private val appVersion = "some app version"
  private val apiVersion = ApiVersions.V01
  private val locale = "some locale"
  private val deviceUuid = RandomService().uuid
  private val deviceModel = DeviceModel(deviceUuid, Option("Some device"),
    Option("crdva"), Option("Some platform"), Option("Some ver"))

  private val apiError = ApiErrorService().badRequest("test error")
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
    (questionMock.answer(_: AnswerModel)(_: ChannelContext)) expects
      (*, *) returning Future.successful(Right(List())) anyNumberOfTimes ()
    (questionMock.getQuestions(_: UUID, _: UUID)(_: ChannelContext)) expects
      (*, *, *) returning Future.successful(Right(List())) anyNumberOfTimes ()
    Services.register(questionMock)
  }

  private def command(implicit input: LoginModel) = new service.LoginCommand(input)

  private def testSuccess(userId: UUID, model: LoginModel, response: LoginResponseModel) {
    val userKey = make[UserKey](userId)
    val deviceKey = make[DeviceKey](deviceUuid)
    val gender = "male"

    val facebook = mock[FacebookService]
    val groupListener = mock[GroupListenerService]
    val unregister = TestServices.register(groupListener, facebook)
    (groupListener.register(_: UUID, _: Set[UUID])(_: ChannelContext)) expects
      (*, *, *) returning Future.successful(Unit)
    (facebook.getInfo(_: String)(_: ChannelContext)) expects
      (*, *) returning Future.successful(Option(FacebookInfo(fbId, name, gender, locale)))
    facebook.getPictureUrl _ expects * returning pictureUrl

    val actual = service.innerExecute(model).await()
    val expected = Right(response)

    debug(actual.toString)
    debug(expected.toString)

    assert(actual == expected)

    assert(userKey.getLastLogin.await().exists(_.isEqual(TimeService().now)))
    assert(userKey.getFacebookId.await().contains(fbId))
    // todo assert(userKey.getNameAndGender.await() == (Option(name), Option(Genders.withName(gender))))
    assert(userKey.getLocale.await().contains(locale))
    assert(userKey.getUpdated.await().exists(_.isEqual(TimeServiceConstantMock.now)))
    assert(deviceKey.getAppVersion.await().contains(appVersion))
    assert(deviceKey.getLocale.await().contains(locale))
    assert(deviceKey.getModel.await() == deviceModel.model)
    assert(deviceKey.getCordova.await() == deviceModel.cordova)
    assert(deviceKey.getPlatform.await() == deviceModel.platform)
    assert(deviceKey.getVersion.await() == deviceModel.version)

    unregister()
  }

  test("without perms") {
    assertPermException(channelCtx => {
      service.execute(model)(channelCtx)
    })
  }

  test("success - new user") {
    val userId = randomMock.nextUuid()
    val userModel = UserModel(userId, Option(pictureUrl), Option(name))
    val myModel = model.copy(groupId = None)
    val response = LoginResponseModelImpl(userModel, None, phoneVerified = false,
      List(), List(), None, None, None)
    testSuccess(userId, myModel, response)
  }

  test("success - existing user - without group id") {
    val userId = RandomService().uuid
    val userModel = UserModel(userId, Option(pictureUrl), Option(name))
    assert(make[FacebookUserKey](fbId).set(userId).await())
    val myModel = model.copy(groupId = None)
    val response = LoginResponseModelImpl(userModel, None, phoneVerified = false,
      List(), List(), None, None, None)
    testSuccess(userId, myModel, response)
  }

  test("success - existing user - with group id") {
    registerQuestionMock()
    val userId = RandomService().uuid
    val userModel = UserModel(userId, Option(pictureUrl), Option(name))
    assert(make[FacebookUserKey](fbId).set(userId).await())
    assert(make[GroupUsersKey](groupId).add(userId).await() == 1L)
    val response = LoginResponseModelImpl(userModel, None, phoneVerified = false,
      List(), List(), Option(List()), Option(List()), None)
    testSuccess(userId, model, response)
  }

  test("failed to authenticate facebook token") {
    val facebook = mock[FacebookService]
    (facebook.getInfo(_: String)(_: ChannelContext)) expects (*, *) returning Future.successful(None)
    val unregister = TestServices.register(facebook)
    assert(command.facebookInfoGet().await() == Errors.tokenInvalid.await())
    unregister()
  }

  test("failed to get groups") {
    val userService = mock[UserService]
    (userService.getGroups(_: UUID)(_: ChannelContext)) expects
      (*, *) returning Future.successful(Left(apiError))
    val unregister = TestServices.register(userService)
    assert(command.groupsGet(RandomService().uuid).await() == Left(apiError))
    unregister()
  }

  test("failed to get group events") {
    val uuid = RandomService().uuid
    val key = mock[UserKey]
    val result = Future.successful(Left(apiError))
    val groupEventsService = mock[GroupEventsService]
    (groupEventsService.getEvents(_: UUID, _: Boolean)(_: ChannelContext)) expects
      (*, *, *) returning result
    val unregister = TestServices.register(groupEventsService)
    assert(command.eventsGet(key, uuid, uuid, LoginResponseModelBuilder()).await() == Left(apiError))
    unregister()
  }

  test("failed to get group questions") {
    val uuid = RandomService().uuid
    val key = mock[UserKey]
    val questionMock = mock[QuestionService]
    (questionMock.getQuestions(_: UUID, _: UUID)(_: ChannelContext)) expects
      (*, *, *) returning Future.successful(Left(apiError))
    val unregister = TestServices.register(questionMock)
    assert(command.eventsGet(key, uuid, uuid, LoginResponseModelBuilder()).await() == Left(apiError))
    unregister()
  }

  test("failed to get user login attributes") {
    val uuid = RandomService().uuid
    val key = mock[UserKey]
    key.getLoginAttributes _ expects () returning Future.failed(new TestRuntimeException)
    intercept[TestRuntimeException] {
      command.userGetLoginAttributes(key, uuid, LoginResponseModelBuilder()).await()
    }
  }

  test("failed to get pending groups") {
    val userId = RandomService().uuid
    val service = mock[UserService]
    (service.getPendingGroups(_: UUID)(_: ChannelContext)) expects
      (*, *) returning Future.successful(Left(apiError))
    assert(command.userGetPendingGroups(service, userId, LoginResponseModelBuilder()).await() == Left(apiError))
  }

}
