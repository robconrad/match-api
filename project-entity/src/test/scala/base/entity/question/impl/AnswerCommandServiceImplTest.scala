/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 11:36 PM
 */

package base.entity.question.impl

import base.common.random.RandomService
import base.common.service.{ TestServices, Services }
import base.entity.auth.context.AuthContextDataFactory
import base.entity.command.impl.CommandServiceImplTest
import base.entity.error.ApiError
import base.entity.kv.KvFactoryService
import base.entity.question.QuestionSides
import base.entity.question.mock.QuestionServiceMock
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

  private val questionsMock = new QuestionServiceMock()

  private implicit val pipeline = KvFactoryService().pipeline
  private implicit val authCtx = AuthContextDataFactory.userAuth
  private implicit val model = AnswerModel(groupId, QuestionSides.SIDE_A, questionResponse)

  override def beforeAll() {
    super.beforeAll()
    Services.register(questionsMock)
  }

  private def command(implicit input: AnswerModel) = new service.AnswerCommand(input)

  test("without perms") {
    assertPermException(authCtx => {
      service.execute(model)(authCtx)
    })
  }

  test("success") {
    assert(service.innerExecute(model).await() == Right(None))
  }

  test("answer set failed") {
    val unregister = TestServices.register(new QuestionServiceMock(answerResult = Future.successful(Left(error))))
    assert(command.answerSet().await() == Left(error))
    unregister()
  }

}
