/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 7:49 PM
 */

package base.entity.question.impl

import base.common.random.RandomService
import base.common.service.TestServices
import base.entity.auth.context.{ ChannelContext, ChannelContextDataFactory }
import base.entity.command.impl.CommandServiceImplTest
import base.entity.command.model.CommandModel
import base.entity.error.ApiErrorService
import base.entity.event.model.EventModel
import base.entity.group.GroupListenerService
import base.entity.question.model.AnswerModel
import base.entity.question.{ QuestionService, QuestionSides }

import scala.concurrent.Future

/**
 * Responsible for testing the logic and side effects of the Impl
 * (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class AnswerCommandServiceImplTest extends CommandServiceImplTest[AnswerModel] {

  val service = new AnswerCommandServiceImpl()

  private val questionResponse = true
  private val groupId = RandomService().uuid

  private val error = ApiErrorService().badRequest("test")

  private implicit val channelCtx = ChannelContextDataFactory.userAuth()
  implicit val model = AnswerModel(RandomService().uuid, groupId, QuestionSides.SIDE_A, questionResponse)

  private def command(implicit input: AnswerModel) = new service.AnswerCommand(input)

  test("success") {
    val eventModel = mock[EventModel]
    val questionService = mock[QuestionService]
    val groupListenerService = mock[GroupListenerService]
    (questionService.answer(_: AnswerModel)(_: ChannelContext)) expects
      (*, *) returning Future.successful(Right(List(eventModel)))
    (groupListenerService.publish(_: CommandModel[EventModel])(_: ChannelContext)) expects
      (*, *) returning Future.successful(Unit)
    val unregister = TestServices.register(questionService, groupListenerService)
    assert(service.innerExecute(model).await() == Right(()))
    unregister()
  }

  test("answer set failed") {
    val questionService = mock[QuestionService]
    (questionService.answer(_: AnswerModel)(_: ChannelContext)) expects
      (*, *) returning Future.successful(Left(error))
    val unregister = TestServices.register(questionService)
    assert(command.answerSet().await() == Left(error))
    unregister()
  }

}
