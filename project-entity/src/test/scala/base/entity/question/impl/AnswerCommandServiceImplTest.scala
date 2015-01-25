/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 12:01 AM
 */

package base.entity.question.impl

import base.common.random.RandomService
import base.common.service.TestServices
import base.entity.auth.context.{ ChannelContext, ChannelContextDataFactory }
import base.entity.command.impl.CommandServiceImplTest
import base.entity.error.ApiError
import base.entity.kv.Key.Pipeline
import base.entity.question.model.AnswerModel
import base.entity.question.{ QuestionService, QuestionSides }

import scala.concurrent.Future

/**
 * Responsible for testing the logic and side effects of the Impl
 * (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class AnswerCommandServiceImplTest extends CommandServiceImplTest {

  val service = new AnswerCommandServiceImpl()

  private val questionResponse = true
  private val groupId = RandomService().uuid

  private val error = ApiError("test")

  private implicit val channelCtx = ChannelContextDataFactory.userAuth
  private implicit val model = AnswerModel(RandomService().uuid, groupId, QuestionSides.SIDE_A, questionResponse)

  private def command(implicit input: AnswerModel) = new service.AnswerCommand(input)

  test("without perms") {
    assertPermException(channelCtx => {
      service.execute(model)(channelCtx)
    })
  }

  test("success") {
    val questionService = mock[QuestionService]
    (questionService.answer(_: AnswerModel)(_: Pipeline, _: ChannelContext)) expects
      (*, *, *) returning Future.successful(Right(List()))
    val unregister = TestServices.register(questionService)
    assert(service.innerExecute(model).await() == Right(()))
    unregister()
  }

  test("answer set failed") {
    val questionService = mock[QuestionService]
    (questionService.answer(_: AnswerModel)(_: Pipeline, _: ChannelContext)) expects
      (*, *, *) returning Future.successful(Left(error))
    val unregister = TestServices.register(questionService)
    assert(command.answerSet().await() == Left(error))
    unregister()
  }

}
