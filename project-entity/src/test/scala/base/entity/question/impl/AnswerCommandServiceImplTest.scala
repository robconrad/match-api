/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 3:03 PM
 */

package base.entity.question.impl

import base.common.random.RandomService
import base.common.service.TestServices
import base.entity.auth.context.{ AuthContext, AuthContextDataFactory }
import base.entity.command.impl.CommandServiceImplTest
import base.entity.error.ApiError
import base.entity.kv.Key.Pipeline
import base.entity.question.{ QuestionService, QuestionSides }
import base.entity.question.model.AnswerModel

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

  private implicit val authCtx = AuthContextDataFactory.userAuth
  private implicit val model = AnswerModel(RandomService().uuid, groupId, QuestionSides.SIDE_A, questionResponse)

  private def command(implicit input: AnswerModel) = new service.AnswerCommand(input)

  test("without perms") {
    assertPermException(authCtx => {
      service.execute(model)(authCtx)
    })
  }

  test("success") {
    val questionService = mock[QuestionService]
    (questionService.answer(_: AnswerModel)(_: Pipeline, _: AuthContext)) expects
      (*, *, *) returning Future.successful(Right(List()))
    val unregister = TestServices.register(questionService)
    assert(service.innerExecute(model).await() == Right(List()))
    unregister()
  }

  test("answer set failed") {
    val questionService = mock[QuestionService]
    (questionService.answer(_: AnswerModel)(_: Pipeline, _: AuthContext)) expects
      (*, *, *) returning Future.successful(Left(error))
    val unregister = TestServices.register(questionService)
    assert(command.answerSet().await() == Left(error))
    unregister()
  }

}
