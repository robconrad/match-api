/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/7/15 3:16 PM
 */

package base.socket.api

import java.util.UUID

import base.common.logging.Loggable
import base.common.random.RandomService
import base.common.random.mock.RandomServiceMock
import base.common.service.{ Services, ServicesBeforeAndAfterAll, TestServices }
import base.common.test.Tags
import base.common.time.mock.TimeServiceConstantMock
import base.entity.api.{ ApiErrorCodes, ApiVersions }
import base.entity.auth.context.impl.ChannelContextImpl
import base.entity.auth.context.{ ChannelContext, StandardUserAuthContext }
import base.entity.command.model.CommandModel
import base.entity.device.model.DeviceModel
import base.entity.error.ApiErrorService
import base.entity.event.EventTypes
import base.entity.event.model.EventModel
import base.entity.event.model.impl.EventModelImpl
import base.entity.facebook.{ FacebookInfo, FacebookService }
import base.entity.group.model._
import base.entity.group.model.impl.{ InviteModelImpl, GroupModelImpl }
import base.entity.json.JsonFormats
import base.entity.kv.Key._
import base.entity.kv.KvTest
import base.entity.message.model.MessageModel
import base.entity.question._
import base.entity.question.impl.QuestionServiceImpl
import base.entity.question.model.{ AnswerModel, QuestionModel, QuestionsModel, QuestionsResponseModel }
import base.entity.sms.mock.SmsServiceMock
import base.entity.user.User
import base.entity.user.impl.{ UserServiceImpl, VerifyPhoneCommandServiceImpl }
import base.entity.user.model._
import base.entity.user.model.impl.LoginResponseModelImpl
import base.socket.api.test.SocketConnection
import base.socket.command.impl.CommandProcessingServiceImpl
import base.socket.test.SocketBaseSuite
import org.joda.time.DateTime
import org.json4s.jackson.JsonMethods
import org.json4s.native.Serialization
import spray.http.StatusCodes

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of SocketApiIntegrationTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
abstract class SocketApiIntegrationTest
    extends SocketBaseSuite
    with ServicesBeforeAndAfterAll
    with Loggable
    with KvTest {

  private implicit val formats = JsonFormats.withModels
  val connectionsAllowed = 6

  private val totalSides = 6
  private val questionDefs = List(
    ("65b76c8e-a9b3-4eda-b6dc-ebee6ef78a04", "Doin' it with the lights off", None),
    ("2c75851f-1a87-40ab-9f66-14766fa12c6c", "Liberal use of chocolate sauce", None),
    ("a8d08d2d-d914-4e7b-8ff4-6a9dde02002c", "Giving buttsex", Option("Receiving buttsex")),
    ("543c57e3-54ba-4d9b-8ed3-c8f2e394b18d", "Dominating", Option("Being dominated"))
  ).map(q => QuestionDef(UUID.fromString(q._1), q._2, q._3))

  private val randomMock = new RandomServiceMock()

  override def beforeAll() {
    super.beforeAll()
    Services.register(randomMock)
    Services.register(TimeServiceConstantMock)
    Services.register(handlerService)
    assert(SocketApiService().start().await())
  }

  override def beforeEach() {
    super.beforeEach()

    // full integration except sms.. don't want to have external side effects
    Services.register(new SmsServiceMock(result = true))

    // use real verify service but swap code validation to be always true
    val codeLength = 6
    Services.register(new VerifyPhoneCommandServiceImpl(codeLength, "whatever") {
      override def validateVerifyCodes(code1: String, code2: String) = true
    })

    // use real user service but ensure a constant ordering of users and groups
    Services.register(new UserServiceImpl() {
      override def getUsers(userId: UUID, userIds: List[UUID])(implicit p: Pipeline, channelCtx: ChannelContext) =
        super.getUsers(userId, userIds)(p, channelCtx).map {
          case Right(users) => Right(sortUsers(users))
          case x            => x
        }
      override def getGroups(userId: UUID)(implicit p: Pipeline, channelCtx: ChannelContext) =
        super.getGroups(userId)(p, channelCtx).map {
          case Right(groups) => Right(sortGroups(groups))
          case x             => x
        }
    })

    // use real question service but control what questions are used and order they are returned
    Services.register(new QuestionServiceImpl(questionDefs, totalSides) {
      override def getQuestions(groupId: UUID, userId: UUID)(implicit p: Pipeline, channelCtx: ChannelContext) = {
        super.getQuestions(groupId, userId)(p, channelCtx).map {
          case Right(questions) => Right(sortQuestions(questions))
        }
      }
    })
  }

  override def afterAll() {
    super.afterAll()
    assert(SocketApiService().stop().await())
  }

  private def sortUsers(users: List[UserModel]) = users.sortBy(_.id.toString)
  private def sortGroups(groups: List[GroupModel]) = groups.sortBy(_.id.toString)
  private def sortQuestions(questions: List[QuestionModel]) = questions.sortBy(q => q.id.toString + q.side)

  def handlerService: SocketApiHandlerService
  def connect(): SocketConnection

  private def execute[A, B](model: A, responseModel: Option[B])(implicit mA: Manifest[A],
                                                                mB: Manifest[B],
                                                                socket: SocketConnection) {
    val command = CommandModel(model)
    val json = Serialization.write(command)

    socket.write(json)

    responseModel foreach { responseModel =>
      assertResponse(responseModel)
    }
  }

  private def assertResponse[B](responseModel: B)(implicit m: Manifest[B], socket: SocketConnection) {
    val rawResponse = socket.read

    val expectedResponse = CommandModel(responseModel)
    val actualResponse = JsonMethods.parse(rawResponse).extract[CommandModel[B]]

    debug(socket.hashCode() + "   actual: " + actualResponse.toString)
    debug(socket.hashCode() + " expected: " + expectedResponse.toString)

    debug(socket.hashCode() + "   pretty actual:\n" + Serialization.writePretty(JsonMethods.parse(rawResponse)))
    debug(socket.hashCode() + " pretty expected:\n" + Serialization.writePretty(expectedResponse))

    assert(actualResponse == expectedResponse)
  }

  test("integration test - runs all commands", Tags.SLOW) {

    // todo remove scalastyle suppression
    // scalastyle:off parameter.number
    def login(deviceId: UUID, fbToken: String, name: String, userId: UUID,
              groups: List[GroupModel], groupId: Option[UUID], phone: Option[String],
              events: Option[List[EventModel]] = None, questions: Option[List[QuestionModel]] = None,
              lastLogin: Option[DateTime] = None)(implicit s: SocketConnection) {
      val deviceModel = DeviceModel(deviceId)
      val loginModel = LoginModel(fbToken, groupId, "", ApiVersions.V01, "", deviceModel)
      val loginResponseModel: LoginResponseModel = LoginResponseModelImpl(UserModel(userId, Option(name)), phone,
        phone.isDefined, List(), sortGroups(groups), events, questions.map(sortQuestions), lastLogin)
      val fbInfo = FacebookInfo(fbToken, name, "male", "EN_us")
      val facebookService = mock[FacebookService]
      val unregister = TestServices.register(facebookService)
      (facebookService.getInfo(_: String)(_: ChannelContext)) expects
        (*, *) returning Future.successful(Option(fbInfo))
      execute(loginModel, Option(loginResponseModel))
      unregister()
    }

    def register(phone: String)(implicit s: SocketConnection) {
      val registerModel = RegisterPhoneModel(phone)
      val registerResponseModel = RegisterPhoneResponseModel(phone)
      execute(registerModel, Option(registerResponseModel))
    }

    def verify(phone: String, pendingGroups: List[GroupModel] = List())(implicit s: SocketConnection) {
      val code = "code!"
      val verifyModel = VerifyPhoneModel(phone, code)
      val verifyResponseModel = VerifyPhoneResponseModel(phone, pendingGroups)
      execute(verifyModel, Option(verifyResponseModel))
    }

    def sendInvite(phone: String, users: List[UserModel], groupId: UUID, events: List[EventModel],
                   questionModels: List[QuestionModel])(implicit s: SocketConnection) {
      val label = "bob"
      val invite = InviteModelImpl(phone, None, Option(label))
      val groupModel = GroupModelImpl(groupId, sortUsers(users), List(invite), None, None, 0)

      val inviteModel = SendInviteModel(phone, label)
      val inviteResponseModel = SendInviteResponseModel(groupModel, events, sortQuestions(questionModels))
      execute(inviteModel, Option(inviteResponseModel))
    }

    def acceptInvite(userId: UUID, users: List[UserModel],
                     groupId: UUID, events: List[EventModel],
                     questionModels: List[QuestionModel])(implicit s: SocketConnection) {
      val groupModel = GroupModelImpl(groupId, sortUsers(users), List(), None, None, 0)

      val acceptInviteModel = AcceptInviteModel(groupId)
      val inviteResponseModel = AcceptInviteResponseModel(groupModel, events, sortQuestions(questionModels))
      execute(acceptInviteModel, Option(inviteResponseModel))
    }

    def declineInvite(groupId: UUID)(implicit s: SocketConnection) {
      val declineInviteModel = DeclineInviteModel(groupId)
      val responseModel = DeclineInviteResponseModel(groupId)
      execute(declineInviteModel, Option(responseModel))
    }

    def questions(groupId: UUID, questionModels: List[QuestionModel])(implicit s: SocketConnection) {
      val questionsModel = QuestionsModel(groupId)
      val questionsResponseModel = QuestionsResponseModel(groupId, sortQuestions(questionModels))
      execute(questionsModel, Option(questionsResponseModel))
    }

    def message(groupId: UUID, event: EventModel)(implicit s: SocketConnection) {
      val messageModel = MessageModel(groupId, event.body)
      execute(messageModel, None)
      assertResponse(event)
    }

    def answer(groupId: UUID, otherUserId: UUID, questionId: UUID)(implicit s: SocketConnection) = {
      val answer = true
      val side = QuestionSides.SIDE_A
      val answerEventId = randomMock.nextUuid()
      val answerBody = questionDefs.find(_.id == questionId).get + " is a match"

      val inviteUserAuthCtx = ChannelContextImpl(new StandardUserAuthContext(new User(otherUserId)), None)
      val inviteUserAnswerModel = AnswerModel(questionId, groupId, side, answer)
      QuestionService().answer(inviteUserAnswerModel)(tp, inviteUserAuthCtx).await()

      val answerModel = AnswerModel(questionId, groupId, side, answer)
      val eventModel: EventModel = EventModelImpl(answerEventId, groupId, None, EventTypes.MATCH, answerBody)
      execute(answerModel, None)
      assertResponse(eventModel)
      eventModel
    }

    val socket1 = connect()

    val fbToken1 = "fbToken1"
    val fbToken2 = "fbToken2"
    val fbToken3 = "fbToken3"

    val name1 = "first name1"
    val name2 = "first name2"
    val name3 = "first name3"

    val deviceId1 = RandomService().uuid
    val userId1 = randomMock.nextUuid()
    val phone1 = "555-1234 (1)"
    val phone2 = "555-4321 (2)"
    val phone3 = "555-4322 (3)"

    login(deviceId1, fbToken1, name1, userId1, groups = List(), None, None)(socket1)

    register(phone1)(socket1)

    verify(phone1)(socket1)

    val groupId = randomMock.nextUuid()
    val eventId = randomMock.nextUuid(1)

    val questionModels = questionDefs.map(QuestionModel(_, QuestionSides.SIDE_A)) ++
      questionDefs.collect {
        case q if q.b.isDefined => QuestionModel(q, QuestionSides.SIDE_B)
      }

    val users1 = List(UserModel(userId1, Option(name1)))
    val events1 = List(EventModelImpl(eventId, groupId, None, EventTypes.MESSAGE,
      "Welcome to Scandal.ly chat! (hush, Michi)"))
    sendInvite(phone2, users1, groupId, events1, questionModels)(socket1)

    questions(groupId, questionModels)(socket1)

    val messageBody = "a message!"
    val messageEventId = randomMock.nextUuid()
    val messageEventModel: EventModel =
      EventModelImpl(messageEventId, groupId, Option(userId1), EventTypes.MESSAGE, messageBody)
    message(groupId, messageEventModel)(socket1)

    val socket2 = connect()
    val deviceId2 = RandomService().uuid
    val userId2 = randomMock.nextUuid()
    login(deviceId2, fbToken2, name2, userId2, List(), None, None)(socket2)

    register(phone2)(socket2)

    val invite2 = InviteModelImpl(phone2, None, Option(name2))
    val pendingGroups2 = List(GroupModelImpl(groupId, users1, List(invite2), None, None, 0))
    verify(phone2, pendingGroups2)(socket2)

    // skip invite for user2

    val users2 = List(
      UserModel(userId1, Option(name1)),
      UserModel(userId2, Option(name2)))
    val eventId2 = randomMock.nextUuid()
    val events2 = events1 ++ List(messageEventModel,
      EventModelImpl(eventId2, groupId, Option(userId2), EventTypes.JOIN,
        "A user joined Scandal.ly chat! (hush, Michi)"))
    acceptInvite(userId2, users2, groupId, events2.reverse, questionModels)(socket2)

    // socket1 answer is down here so that we have a valid match user id
    val answerEvent = answer(groupId, userId2, questionDefs(0).id)(socket1)
    assertResponse(answerEvent)(manifest[EventModel], socket2)

    val questionModels2 = questionModels.filter(_.id != questionDefs(0).id)
    questions(groupId, questionModels2)(socket2)

    val messageEventId2 = randomMock.nextUuid()
    val messageEventModel2: EventModel =
      EventModelImpl(messageEventId2, groupId, Option(userId2), EventTypes.MESSAGE, messageBody)
    message(groupId, messageEventModel2)(socket2)
    assertResponse(messageEventModel2)(manifest[EventModel], socket1)

    val answerEvent2 = answer(groupId, userId1, questionDefs(1).id)(socket2)
    assertResponse(answerEvent2)(manifest[EventModel], socket1)

    val socket3 = connect()
    val deviceId3 = RandomService().uuid
    val userId3 = randomMock.nextUuid()
    login(deviceId3, fbToken3, name3, userId3, List(), None, None)(socket3)

    val groupId2 = randomMock.nextUuid()
    val events3 = List(EventModelImpl(randomMock.nextUuid(1), groupId2, None, EventTypes.MESSAGE,
      "Welcome to Scandal.ly chat! (hush, Michi)"))
    sendInvite(phone3, users1, groupId2, events3, questionModels)(socket1)

    register(phone3)(socket3)

    val invite3 = InviteModelImpl(phone3, None, Option(name3))
    val pendingGroups3 = List(GroupModelImpl(groupId2, users1, List(invite3), None, None, 0))
    verify(phone3, pendingGroups3)(socket3)

    declineInvite(groupId2)(socket3)

    socket1.disconnect()
    socket2.disconnect()
    socket3.disconnect()

    // original user login again
    val socket4 = connect()
    val groups = List(
      GroupModelImpl(groupId, sortUsers(users2), List(), None, None, 0),
      GroupModelImpl(groupId2, sortUsers(users2.slice(0, 1)), List(invite3), None, None, 0))
    login(deviceId1, fbToken1, name1, userId1, groups, Option(groupId), Option(phone1),
      Option((events2 ++ List(messageEventModel2)).reverse), Option(questionModels2.filter(_.id != questionDefs(1).id)),
      Option(TimeServiceConstantMock.now))(socket4)
  }

  test("error", Tags.SLOW) {
    implicit val socket = connect()
    assert(socket.isActive)
    socket.write("")
    assertResponse(ApiErrorService().errorCodeSeed(
      CommandProcessingServiceImpl.Errors.externalErrorText,
      StatusCodes.BadRequest,
      ApiErrorCodes.JSON_NOT_FOUND,
      "no json received in msg: JNothing"))
    assert(!socket.isActive)
  }

}
