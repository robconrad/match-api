/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 9:45 AM
 */

package base.socket.api

import java.util.UUID

import base.common.lib.Genders
import base.common.logging.Loggable
import base.common.random.RandomService
import base.common.random.mock.RandomServiceMock
import base.common.service.{ Services, ServicesBeforeAndAfterAll }
import base.common.test.Tags
import base.common.time.mock.TimeServiceConstantMock
import base.entity.api.ApiVersions
import base.entity.auth.context.impl.ChannelContextImpl
import base.entity.auth.context.{ ChannelContext, StandardUserAuthContext }
import base.entity.command.model.CommandModel
import base.entity.device.model.DeviceModel
import base.entity.event.EventTypes
import base.entity.event.model.EventModel
import base.entity.group.model.{ GroupModel, InviteModel, InviteResponseModel }
import base.entity.json.JsonFormats
import base.entity.kv.Key._
import base.entity.kv.KvTest
import base.entity.message.model.MessageModel
import base.entity.question._
import base.entity.question.impl.QuestionServiceImpl
import base.entity.question.model.{ AnswerModel, QuestionModel, QuestionsModel, QuestionsResponseModel }
import base.entity.sms.mock.SmsServiceMock
import base.entity.user.User
import base.entity.user.impl.{ UserServiceImpl, VerifyCommandServiceImpl }
import base.entity.user.model._
import base.socket.api.test.SocketConnection
import base.socket.test.SocketBaseSuite
import org.json4s.jackson.JsonMethods
import org.json4s.native.Serialization

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

  private implicit val formats = JsonFormats.withEnumsAndFields
  val connectionsAllowed = 6

  private val totalSides = 6
  private val questionDefs = List(
    ("65b76c8e-a9b3-4eda-b6dc-ebee6ef78a04", "Doin' it with the lights off", None),
    ("2c75851f-1a87-40ab-9f66-14766fa12c6c", "Liberal use of chocolate sauce", None),
    ("a8d08d2d-d914-4e7b-8ff4-6a9dde02002c", "Giving buttsex", Option("Receiving buttsex")),
    ("543c57e3-54ba-4d9b-8ed3-c8f2e394b18d", "Dominating", Option("Being dominated"))
  ).map(q => QuestionDef(UUID.fromString(q._1), q._2, q._3))

  private val randomMock = new RandomServiceMock()
  private val time = TimeServiceConstantMock.now

  override def beforeAll() {
    super.beforeAll()
    Services.register(handlerService)
    assert(SocketApiService().start().await())
  }

  override def beforeEach() {
    super.beforeEach()

    // full integration except sms.. don't want to have external side effects
    Services.register(new SmsServiceMock(result = true))

    // use real verify service but swap code validation to be always true
    val codeLength = 6
    Services.register(new VerifyCommandServiceImpl(codeLength, "whatever") {
      override def validateVerifyCodes(code1: String, code2: String) = true
    })

    // use real user service but ensure a constant ordering of users
    Services.register(new UserServiceImpl() {
      override def getUsers(userId: UUID, userIds: List[UUID])(implicit p: Pipeline, channelCtx: ChannelContext) =
        super.getUsers(userId, userIds)(p, channelCtx).map {
          case Right(users) => Right(sortUsers(users))
          case x            => x
        }
    })

    // use real question service but control what questions are used and order they are returned
    Services.register(new QuestionServiceImpl(questionDefs, totalSides) {
      override def getQuestions(groupId: UUID)(implicit p: Pipeline, channelCtx: ChannelContext) = {
        super.getQuestions(groupId)(p, channelCtx).map {
          case Right(questions) => Right(sortQuestions(questions))
        }
      }
    })

    Services.register(randomMock)
    Services.register(TimeServiceConstantMock)
  }

  override def afterAll() {
    super.afterAll()
    assert(SocketApiService().stop().await())
  }

  private def sortUsers(users: List[UserModel]) = users.sortBy(_.id.toString)
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
    val expectedResponse = CommandModel(responseModel)
    val actualResponse = JsonMethods.parse(socket.read).extract[CommandModel[B]]

    debug(socket.hashCode() + "   actual: " + actualResponse.toString)
    debug(socket.hashCode() + " expected: " + expectedResponse.toString)

    assert(actualResponse == expectedResponse)
  }

  test("integration test - runs all commands", Tags.SLOW) {

    def register(phone: String)(implicit s: SocketConnection) {
      val registerModel = RegisterModel(ApiVersions.V01, phone)
      val registerResponseModel = RegisterResponseModel()
      execute(registerModel, Option(registerResponseModel))
    }

    def verify(phone: String, deviceId: UUID, token: UUID)(implicit s: SocketConnection) {
      val code = "code!"
      val verifyModel = VerifyModel(ApiVersions.V01, Option("name"), Option(Genders.other), phone, deviceId, code)
      val verifyResponseModel = VerifyResponseModel(token)
      execute(verifyModel, Option(verifyResponseModel))
    }

    def login(deviceId: UUID, token: UUID, userId: UUID, groups: List[GroupModel])(implicit s: SocketConnection) {
      val deviceModel = DeviceModel(deviceId, "", "", "", "", "")
      val loginModel = LoginModel(token, None, "", ApiVersions.V01, "", deviceModel)
      val loginResponseModel = LoginResponseModel(userId, groups, None, None, None)
      execute(loginModel, Option(loginResponseModel))
    }

    def invite(phone: String, userId: UUID, inviteUserId: UUID, groupId: UUID)(implicit s: SocketConnection) {
      val label = "bob"
      val userModel = UserModel(userId, None)
      val inviteUserModel = UserModel(inviteUserId, Option(label))

      val groupModel = GroupModel(groupId, sortUsers(List(userModel, inviteUserModel)), None, None, 0)
      val inviteModel = InviteModel(phone, label)
      val inviteResponseModel = InviteResponseModel(inviteUserId, groupModel)
      execute(inviteModel, Option(inviteResponseModel))
    }

    def questions(groupId: UUID, questionModels: List[QuestionModel])(implicit s: SocketConnection) {
      val questionsModel = QuestionsModel(groupId)
      val questionsResponseModel = QuestionsResponseModel(groupId, sortQuestions(questionModels))
      execute(questionsModel, Option(questionsResponseModel))
    }

    def message(groupId: UUID, userId: UUID)(implicit s: SocketConnection) = {
      val messageBody = "a message!"
      val messageEventId = randomMock.nextUuid()

      val messageModel = MessageModel(groupId, messageBody)
      val eventModel = EventModel(messageEventId, groupId, Option(userId), EventTypes.MESSAGE, messageBody, time)
      execute(messageModel, None)
      assertResponse(eventModel)
      eventModel
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
      val eventModel = EventModel(answerEventId, groupId, None, EventTypes.MATCH, answerBody, time)
      execute(answerModel, None)
      assertResponse(eventModel)
      eventModel
    }

    val socket = connect()

    val deviceId = RandomService().uuid
    val userId = randomMock.nextUuid()
    val phone = "555-1234"
    val phone2 = "555-4321"

    register(phone)(socket)

    val token = randomMock.nextUuid()
    verify(phone, deviceId, token)(socket)

    login(deviceId, token, userId, groups = List())(socket)

    val inviteUserId = randomMock.nextUuid()
    val groupId = randomMock.nextUuid(1)
    invite(phone2, userId, inviteUserId, groupId)(socket)

    val questionModels = questionDefs.map(QuestionModel(_, QuestionSides.SIDE_A)) ++
      questionDefs.collect {
        case q if q.b.isDefined => QuestionModel(q, QuestionSides.SIDE_B)
      }
    questions(groupId, questionModels)(socket)

    message(groupId, userId)(socket)

    answer(groupId, inviteUserId, questionDefs(0).id)(socket)

    val socket2 = connect()
    val deviceId2 = RandomService().uuid

    register(phone2)(socket2)

    val token2 = randomMock.nextUuid()
    verify(phone2, deviceId2, token2)(socket2)

    val users = sortUsers(List(UserModel(userId, None), UserModel(inviteUserId, None)))
    val groups = List(GroupModel(groupId, users, None, None, 0))
    login(deviceId2, token2, inviteUserId, groups)(socket2)

    // skip invite for user2

    val questionModels2 = questionModels.filter(_.id != questionDefs(0).id)
    questions(groupId, questionModels2)(socket2)

    val messageEvent = message(groupId, inviteUserId)(socket2)
    assertResponse(messageEvent)(manifest[EventModel], socket)

    val answerEvent = answer(groupId, userId, questionDefs(1).id)(socket2)
    assertResponse(answerEvent)(manifest[EventModel], socket)

    socket.disconnect()
    socket2.disconnect()
  }

}
