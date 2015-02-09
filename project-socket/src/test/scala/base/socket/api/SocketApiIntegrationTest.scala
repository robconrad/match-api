/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 4:53 PM
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
import base.entity.json.JsonFormats
import base.entity.kv.Key._
import base.entity.kv.KvTest
import base.entity.question.impl.QuestionServiceImpl
import base.entity.sms.mock.SmsServiceMock
import base.entity.user.impl.{UserServiceImpl, VerifyPhoneCommandServiceImpl}
import base.socket.api.test.IntegrationSuite
import base.socket.api.test.command.CommandExecutor
import base.socket.api.test.util.ListUtils._
import base.socket.api.test.util.TestQuestions
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

  test("integration test - runs all commands", Tags.SLOW) {
    val (socket1, socket1a, socket2, socket3) = (makeSocket(), makeSocket(), makeSocket(), makeSocket())

    socket1.connect()

    socket1.login()
    socket1.register()
    socket1.verify()
    val group1 = socket1.sendInvite(socket2)
    socket1.questions(group1.id)
    socket1.message(group1)

    socket2.connect()

    socket2.login()
    group1.invites = List(socket2.inviteModel)
    socket2.register()
    socket2.verify(List(group1))
    socket2.acceptInvite(group1)
    socket1.answer(randomMock.nextUuid(), group1, socket2.userId, questionIndex = 0)
    socket2.questions(group1.id, List(0))
    socket2.message(group1)
    socket2.answer(randomMock.nextUuid(1), group1, socket1.userId, questionIndex = 1)

    socket3.connect()

    socket3.login()
    val group2 = socket1.sendInvite(socket3)
    socket3.register()
    group2.invites = List(socket3.inviteModel)
    socket3.verify(List(group2))
    socket3.declineInvite(group2.id)

    socket1.disconnect()
    socket2.disconnect()
    socket3.disconnect()

    socket1a.props = socket1.props
    socket1a.connect()

    // original user login again
    socket1a.login(
      List(group1, group2),
      Option(group1.id),
      Option(socket1.phone),
      Option(group1.events.reverse),
      Option(List(0)),
      Option(TimeServiceConstantMock.now))

    socket1a.disconnect()
  }

  test("error", Tags.SLOW) {
    implicit val socket = makeSocket().connect()
    assert(socket.isActive)
    socket.write("")
    executor.assertResponse(ApiErrorService().errorCodeSeed(
      CommandProcessingServiceImpl.Errors.externalErrorText,
      StatusCodes.BadRequest,
      ApiErrorCodes.JSON_NOT_FOUND,
      "no json received in msg: JNothing"))
    assert(!socket.isActive)

    socket.disconnect()
  }

}
