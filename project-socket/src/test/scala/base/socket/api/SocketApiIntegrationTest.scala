/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 12:50 PM
 */

package base.socket.api

import java.util.UUID

import base.common.logging.Loggable
import base.common.random.RandomService
import base.common.random.mock.RandomServiceMock
import base.common.service.{Services, ServicesBeforeAndAfterAll}
import base.common.test.Tags
import base.common.time.mock.TimeServiceConstantMock
import base.entity.api.ApiErrorCodes
import base.entity.auth.context.ChannelContext
import base.entity.error.ApiErrorService
import base.entity.event.EventTypes
import base.entity.event.model.EventModel
import base.entity.event.model.impl.EventModelImpl
import base.entity.group.model.impl.GroupModelImpl
import base.entity.json.JsonFormats
import base.entity.kv.Key._
import base.entity.kv.KvTest
import base.entity.question._
import base.entity.question.impl.QuestionServiceImpl
import base.entity.question.model.QuestionModel
import base.entity.sms.mock.SmsServiceMock
import base.entity.user.impl.{UserServiceImpl, VerifyPhoneCommandServiceImpl}
import base.socket.api.test.ListUtils._
import base.socket.api.test.command.CommandExecutor
import base.socket.api.test.{IntegrationSuite, SocketConnection, SocketProperties}
import base.socket.command.impl.CommandProcessingServiceImpl
import spray.http.StatusCodes

/**
 * {{ Describe the high level purpose of SocketApiIntegrationTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
abstract class SocketApiIntegrationTest
    extends IntegrationSuite
    with ServicesBeforeAndAfterAll
    with Loggable
    with KvTest {

  private implicit val executor = new CommandExecutor()
  private implicit val formats = JsonFormats.withModels
  val connectionsAllowed = 6

  private val totalSides = 6
  private implicit val questionDefs = List(
    ("65b76c8e-a9b3-4eda-b6dc-ebee6ef78a04", "Doin' it with the lights off", None),
    ("2c75851f-1a87-40ab-9f66-14766fa12c6c", "Liberal use of chocolate sauce", None),
    ("a8d08d2d-d914-4e7b-8ff4-6a9dde02002c", "Giving buttsex", Option("Receiving buttsex")),
    ("543c57e3-54ba-4d9b-8ed3-c8f2e394b18d", "Dominating", Option("Being dominated"))
  ).map(q => QuestionDef(UUID.fromString(q._1), q._2, q._3))

  private implicit val randomMock = new RandomServiceMock()

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

  implicit def socket2Props(s: SocketConnection): SocketProperties = s.props

  test("integration test - runs all commands", Tags.SLOW) {

    val socket1 = connect()
    val phone2 = "phone 2"

    val deviceId1 = RandomService().uuid
    val userId1 = randomMock.nextUuid()

    socket1.deviceId = deviceId1
    socket1.userId = userId1
    socket1.login(groups = List(), None, None)

    socket1.register()

    socket1.verify()

    val groupId = randomMock.nextUuid()
    val eventId = randomMock.nextUuid(1)

    val questionModels = questionDefs.map(QuestionModel(_, QuestionSides.SIDE_A)) ++
      questionDefs.collect {
        case q if q.b.isDefined => QuestionModel(q, QuestionSides.SIDE_B)
      }

    val users1 = List(socket1.userModel)
    val events1 = List(EventModelImpl(eventId, groupId, None, EventTypes.MESSAGE,
      "Welcome to Scandal.ly chat! (hush, Michi)"))
    socket1.sendInvite(phone2, users1, groupId, events1, questionModels)

    socket1.questions(groupId, questionModels)

    val messageBody = "a message!"
    val messageEventId = randomMock.nextUuid()
    val messageEventModel: EventModel =
      EventModelImpl(messageEventId, groupId, Option(userId1), EventTypes.MESSAGE, messageBody)
    socket1.message(groupId, messageEventModel)

    val socket2 = connect(new SocketProperties(_phone = Option(phone2)))
    val deviceId2 = RandomService().uuid
    val userId2 = randomMock.nextUuid()
    socket2.deviceId = deviceId2
    socket2.userId = userId2
    socket2.login(List(), None, None)

    socket2.register()

    val pendingGroups2 = List(GroupModelImpl(groupId, users1, List(socket2.inviteModel), None, None, 0))
    socket2.verify(pendingGroups2)

    // skip invite for user2

    val users2 = List(socket1.userModel, socket2.userModel)
    val eventId2 = randomMock.nextUuid()
    val events2 = events1 ++ List(messageEventModel,
      EventModelImpl(eventId2, groupId, Option(userId2), EventTypes.JOIN,
        "A user joined Scandal.ly chat! (hush, Michi)"))
    socket2.acceptInvite(users2, groupId, events2.reverse, questionModels)

    // socket1 answer is down here so that we have a valid match user id
    val answerEvent = socket1.answer(groupId, userId2, questionDefs(0).id)
    executor.assertResponse(answerEvent)(manifest[EventModel], socket2)

    val questionModels2 = questionModels.filter(_.id != questionDefs(0).id)
    socket2.questions(groupId, questionModels2)

    val messageEventId2 = randomMock.nextUuid()
    val messageEventModel2: EventModel =
      EventModelImpl(messageEventId2, groupId, Option(userId2), EventTypes.MESSAGE, messageBody)
    socket2.message(groupId, messageEventModel2)
    executor.assertResponse(messageEventModel2)(manifest[EventModel], socket1)

    val answerEvent2 = socket2.answer(groupId, userId1, questionDefs(1).id)
    executor.assertResponse(answerEvent2)(manifest[EventModel], socket1)

    val socket3 = connect()
    val deviceId3 = RandomService().uuid
    val userId3 = randomMock.nextUuid()
    socket3.deviceId = deviceId3
    socket3.userId = userId3
    socket3.login(List(), None, None)

    val groupId2 = randomMock.nextUuid()
    val events3 = List(EventModelImpl(randomMock.nextUuid(1), groupId2, None, EventTypes.MESSAGE,
      "Welcome to Scandal.ly chat! (hush, Michi)"))
    socket1.sendInvite(socket3.phone, users1, groupId2, events3, questionModels)

    socket3.register()

    val pendingGroups3 = List(GroupModelImpl(groupId2, users1, List(socket3.inviteModel), None, None, 0))
    socket3.verify(pendingGroups3)

    socket3.declineInvite(groupId2)

    socket1.disconnect()
    socket2.disconnect()
    socket3.disconnect()

    // original user login again
    val socket4 = connect(socket1.props)
    val groups = List(
      GroupModelImpl(groupId, sortUsers(users2), List(), None, None, 0),
      GroupModelImpl(groupId2, sortUsers(users2.slice(0, 1)), List(socket3.inviteModel), None, None, 0))
    socket4.login(groups, Option(groupId), Option(socket1.phone),
      Option((events2 ++ List(messageEventModel2)).reverse), Option(questionModels2.filter(_.id != questionDefs(1).id)),
      Option(TimeServiceConstantMock.now))
  }

  test("error", Tags.SLOW) {
    implicit val socket = connect()
    assert(socket.isActive)
    socket.write("")
    executor.assertResponse(ApiErrorService().errorCodeSeed(
      CommandProcessingServiceImpl.Errors.externalErrorText,
      StatusCodes.BadRequest,
      ApiErrorCodes.JSON_NOT_FOUND,
      "no json received in msg: JNothing"))
    assert(!socket.isActive)
  }

}
