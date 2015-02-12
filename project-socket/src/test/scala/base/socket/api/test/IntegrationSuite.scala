/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 10:34 PM
 */

package base.socket.api.test

import java.util.UUID

import base.common.random.mock.RandomServiceMock
import base.common.service.{ Services, ServicesBeforeAndAfterAll }
import base.common.time.mock.TimeServiceConstantMock
import base.entity.auth.context.ChannelContext
import base.entity.kv.KvTest
import base.entity.question.impl.QuestionServiceImpl
import base.entity.sms.mock.SmsServiceMock
import base.entity.user.impl.{ UserServiceImpl, VerifyPhoneCommandServiceImpl }
import base.socket.api.test.command.CommandExecutor
import base.socket.api.test.util.ListUtils._
import base.socket.api.test.util.TestQuestions
import base.socket.api.{ SocketApiHandlerService, SocketApiService }
import base.socket.test.SocketBaseSuite

/**
 * {{ Describe the high level purpose of IntegrationSuite here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
private[api] trait IntegrationSuite extends SocketBaseSuite with ServicesBeforeAndAfterAll with KvTest {

  implicit val executor = new CommandExecutor()
  implicit val questions = new TestQuestions
  implicit val randomMock = new RandomServiceMock()

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
      override def getUsers(userId: UUID, userIds: List[UUID])(implicit channelCtx: ChannelContext) =
        super.getUsers(userId, userIds)(channelCtx).map {
          case Right(users) => Right(sortUsers(users))
          case x            => x
        }

      override def getGroups(userId: UUID)(implicit channelCtx: ChannelContext) =
        super.getGroups(userId)(channelCtx).map {
          case Right(groups) => Right(sortGroups(groups))
          case x             => x
        }
    })

    // use real question service but control what questions are used and order they are returned
    val questionCount = 25
    Services.register(new QuestionServiceImpl(questions.standardDefs, questionCount, questionCount / 10) {
      override def getQuestions(groupId: UUID, userId: UUID)(implicit channelCtx: ChannelContext) = {
        super.getQuestions(groupId, userId)(channelCtx).map {
          case Right(questions) => Right(sortQuestions(questions))
          case Left(e) => Left(e)
        }
      }
    })
  }

  override def afterAll() {
    super.afterAll()
    assert(SocketApiService().stop().await())
  }

  def handlerService: SocketApiHandlerService

  def makeGroup() = new TestGroup()

  def makeSocket(): SocketConnection = makeSocket(new SocketProperties())
  def makeSocket(props: SocketProperties): SocketConnection

}
