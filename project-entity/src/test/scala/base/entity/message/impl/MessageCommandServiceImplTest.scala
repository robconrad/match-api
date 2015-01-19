/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 4:01 PM
 */

package base.entity.message.impl

import base.common.random.RandomService
import base.common.random.mock.RandomServiceMock
import base.common.service.{ Services, TestServices }
import base.common.time.mock.TimeServiceConstantMock
import base.entity.auth.context.AuthContextDataFactory
import base.entity.command.impl.CommandServiceImplTest
import base.entity.error.ApiError
import base.entity.event.EventTypes
import base.entity.event.model.EventModel
import base.entity.group.GroupEventsService
import base.entity.kv.Key._
import base.entity.message.model.MessageModel

import scala.concurrent.Future

/**
 * Responsible for testing the logic and side effects of the Impl
 * (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class MessageCommandServiceImplTest extends CommandServiceImplTest {

  val service = new MessageCommandServiceImpl()

  private val body = "a message"
  private val groupId = RandomService().uuid

  private val error = ApiError("test")

  private val randomMock = new RandomServiceMock()

  private implicit val authCtx = AuthContextDataFactory.userAuth
  private implicit val model = MessageModel(groupId, body)

  override def beforeAll() {
    super.beforeAll()
    Services.register(TimeServiceConstantMock)
    Services.register(randomMock)
  }

  private def command(implicit input: MessageModel) = new service.MessageCommand(input)

  test("without perms") {
    assertPermException(authCtx => {
      service.execute(model)(authCtx)
    })
  }

  test("success") {
    val messageId = randomMock.nextUuid()
    val event = EventModel(messageId, groupId, Option(authCtx.userId), EventTypes.MESSAGE, body)
    val groupEventsService = mock[GroupEventsService]
    (groupEventsService.setEvent(_: EventModel, _: Boolean)(_: Pipeline)) expects
      (*, *, *) returning Future.successful(Right(event))
    val unregister = TestServices.register(groupEventsService)
    assert(service.innerExecute(model).await() == Right(event))
    unregister()
  }

  test("group event set failed") {
    val groupEventsService = mock[GroupEventsService]
    (groupEventsService.setEvent(_: EventModel, _: Boolean)(_: Pipeline)) expects
      (*, *, *) returning Future.successful(Left(error))
    val unregister = TestServices.register(groupEventsService)
    assert(command.groupEventSet().await() == Left(error))
    unregister()
  }

}
