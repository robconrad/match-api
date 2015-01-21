/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/20/15 10:58 PM
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
import base.entity.auth.context.{ AuthContext, StandardUserAuthContext }
import base.entity.command.CommandServiceCompanion
import base.entity.command.model.CommandModel
import base.entity.device.model.DeviceModel
import base.entity.event.EventTypes
import base.entity.event.model.EventModel
import base.entity.group.InviteCommandService
import base.entity.group.model.{ GroupModel, InviteModel, InviteResponseModel }
import base.entity.json.JsonFormats
import base.entity.kv.Key._
import base.entity.kv.KvTest
import base.entity.message.MessageCommandService
import base.entity.message.model.MessageModel
import base.entity.question._
import base.entity.question.impl.QuestionServiceImpl
import base.entity.question.model.{ AnswerModel, QuestionModel, QuestionsModel, QuestionsResponseModel }
import base.entity.sms.mock.SmsServiceMock
import base.entity.user.impl.{ UserServiceImpl, VerifyCommandServiceImpl }
import base.entity.user.model._
import base.entity.user.{ LoginCommandService, RegisterCommandService, User, VerifyCommandService }
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
  private val questions = List(
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
      override def getUsers(userIds: List[UUID])(implicit p: Pipeline, authCtx: AuthContext) =
        super.getUsers(userIds)(p, authCtx).map {
          case Right(users) => Right(users.sortBy(_.label.getOrElse("")))
          case x            => x
        }
    })

    // use real question service but control what questions are used and order they are returned
    Services.register(new QuestionServiceImpl(questions, totalSides) {
      override def getQuestions(groupId: UUID)(implicit p: Pipeline, authCtx: AuthContext) = {
        super.getQuestions(groupId)(p, authCtx).map {
          case Right(questions) => Right(questions.sortBy(q => q.id.toString + q.side))
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

  def handlerService: SocketApiHandlerService
  def connect()
  def disconnect()
  def writeRead(json: String): String

  private def execute[A, B](companion: CommandServiceCompanion[_],
                            model: A, responseModel: B)(implicit m: Manifest[B]) {

    val command = CommandModel(companion.inCmd, model)
    val json = Serialization.write(command)

    val expectedResponse = CommandModel(companion.outCmd, responseModel)
    val actualResponse = JsonMethods.parse(writeRead(json)).extract[CommandModel[B]]

    debug("  actual: " + actualResponse.toString)
    debug("expected: " + expectedResponse.toString)

    assert(actualResponse == expectedResponse)
  }

  test("integration test - runs all commands", Tags.SLOW) {
    connect()

    val phone = "555-1234"
    val deviceId = RandomService().uuid
    val userId = randomMock.nextUuid()

    val registerModel = RegisterModel(ApiVersions.V01, phone)
    val registerResponseModel = RegisterResponseModel()
    execute(RegisterCommandService, registerModel, registerResponseModel)

    val code = "code!"
    val token = randomMock.nextUuid()

    val verifyModel = VerifyModel(ApiVersions.V01, Option("name"), Option(Genders.other), phone, deviceId, code)
    val verifyResponseModel = VerifyResponseModel(token)
    execute(VerifyCommandService, verifyModel, verifyResponseModel)

    val deviceModel = DeviceModel(deviceId, "", "", "", "", "")
    val loginModel = LoginModel(token, None, "", ApiVersions.V01, "", deviceModel)
    val loginResponseModel = LoginResponseModel(userId, List(), None, None, None)
    execute(LoginCommandService, loginModel, loginResponseModel)

    val inviteUserId = randomMock.nextUuid()
    val groupId = randomMock.nextUuid(1)
    val label = "bob"
    val userModel = UserModel(userId, None)
    val inviteUserModel = UserModel(inviteUserId, Option(label))

    val groupModel = GroupModel(groupId, List(userModel, inviteUserModel), None, None, 0)
    val inviteModel = InviteModel("555-5432", label)
    val inviteResponseModel = InviteResponseModel(inviteUserId, groupModel)
    execute(InviteCommandService, inviteModel, inviteResponseModel)

    val questionModels = questions.map(QuestionModel(_, QuestionSides.SIDE_A)) ++
      questions.collect {
        case q if q.b.isDefined => QuestionModel(q, QuestionSides.SIDE_B)
      }
    val questionsModel = QuestionsModel(groupId)
    val questionsResponseModel = QuestionsResponseModel(groupId, questionModels.sortBy(q => q.id.toString + q.side))
    execute(QuestionsCommandService, questionsModel, questionsResponseModel)

    val messageBody = "a message!"
    val messageEventId = randomMock.nextUuid()

    val messageModel = MessageModel(groupId, messageBody)
    val eventModel = EventModel(messageEventId, groupId, Option(userId), EventTypes.MESSAGE, messageBody, time)
    execute(MessageCommandService, messageModel, eventModel)

    val answer = true
    val questionId = questions(0).id
    val side = QuestionSides.SIDE_A
    val answerEventId = randomMock.nextUuid()
    val answerBody = questions(0) + " is a match"

    val inviteUserAuthCtx = new StandardUserAuthContext(new User(inviteUserId))
    val inviteUserAnswerModel = AnswerModel(questionId, groupId, side, answer)
    QuestionService().answer(inviteUserAnswerModel)(tp, inviteUserAuthCtx).await()

    val answerModel = AnswerModel(questionId, groupId, side, answer)
    val answerResponseModel = List(EventModel(answerEventId, groupId, None, EventTypes.MATCH, answerBody, time))
    execute(AnswerCommandService, answerModel, answerResponseModel)

    disconnect()
  }

}
