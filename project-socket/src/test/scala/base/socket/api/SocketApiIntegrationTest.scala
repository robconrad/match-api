/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 1:39 PM
 */

package base.socket.api

import java.util.UUID

import base.common.logging.Loggable
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
import base.entity.question.impl.QuestionServiceImpl
import base.entity.sms.mock.SmsServiceMock
import base.entity.user.impl.{UserServiceImpl, VerifyPhoneCommandServiceImpl}
import base.socket.api.test.command.CommandExecutor
import base.socket.api.test.utils.ListUtils._
import base.socket.api.test.utils.TestQuestions
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
  private implicit val questions = new TestQuestions

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
    Services.register(new QuestionServiceImpl(questions.defs, totalSides) {
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
    val phone2 = "phone 2"

    val userId1 = randomMock.nextUuid(2)
    val socket1 = connect(new SocketProperties(_userId = Option(userId1)))

    socket1.login(groups = List(), None, None)
    socket1.register()
    socket1.verify()

    val groupId = randomMock.nextUuid()
    val eventId = randomMock.nextUuid(1)

    val users1 = List(socket1.userModel)
    val events1 = List(EventModelImpl(eventId, groupId, None, EventTypes.MESSAGE,
      "Welcome to Scandal.ly chat! (hush, Michi)"))

    socket1.sendInvite(phone2, users1, groupId, events1)
    socket1.questions(groupId)

    val messageBody = "a message!"
    val messageEventId = randomMock.nextUuid()
    val messageEventModel: EventModel =
      EventModelImpl(messageEventId, groupId, Option(socket1.userId), EventTypes.MESSAGE, messageBody)

    socket1.message(groupId, messageEventModel)

    val userId2 = randomMock.nextUuid(2)
    val socket2 = connect(new SocketProperties(_userId = Option(userId2), _phone = Option(phone2)))

    socket2.login(List(), None, None)
    socket2.register()

    val pendingGroups2 = List(GroupModelImpl(groupId, users1, List(socket2.inviteModel), None, None, 0))

    socket2.verify(pendingGroups2)

    val users2 = List(socket1.userModel, socket2.userModel)
    val eventId2 = randomMock.nextUuid()
    val events2 = events1 ++ List(messageEventModel,
      EventModelImpl(eventId2, groupId, Option(userId2), EventTypes.JOIN,
        "A user joined Scandal.ly chat! (hush, Michi)"))

    socket2.acceptInvite(users2, groupId, events2.reverse)

    // socket1 answer is down here so that we have a valid match user id
    val answerEvent = socket1.answer(randomMock.nextUuid(), groupId, userId2, questionIndex = 0)
    executor.assertResponse(answerEvent)(manifest[EventModel], socket2)

    socket2.questions(groupId, List(0))

    val messageEventId2 = randomMock.nextUuid()
    val messageEventModel2: EventModel =
      EventModelImpl(messageEventId2, groupId, Option(userId2), EventTypes.MESSAGE, messageBody)

    socket2.message(groupId, messageEventModel2)
    executor.assertResponse(messageEventModel2)(manifest[EventModel], socket1)

    val answerEvent2 = socket2.answer(randomMock.nextUuid(1), groupId, socket1.userId, questionIndex = 1)
    executor.assertResponse(answerEvent2)(manifest[EventModel], socket1)

    val userId3 = randomMock.nextUuid(2)
    val socket3 = connect(new SocketProperties(_userId = Option(userId3)))
    socket3.login(List(), None, None)

    val groupId2 = randomMock.nextUuid()
    val events3 = List(EventModelImpl(randomMock.nextUuid(1), groupId2, None, EventTypes.MESSAGE,
      "Welcome to Scandal.ly chat! (hush, Michi)"))

    socket1.sendInvite(socket3.phone, users1, groupId2, events3)
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
      Option((events2 ++ List(messageEventModel2)).reverse), Option(List(0)),
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
