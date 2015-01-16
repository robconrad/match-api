/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 11:24 PM
 */

package base.entity.question.impl

import base.common.random.RandomService
import base.common.service.{ TestServices, Services }
import base.common.time.mock.TimeServiceConstantMock
import base.entity.auth.context.AuthContextDataFactory
import base.entity.command.impl.CommandServiceImplTest
import base.entity.error.ApiError
import base.entity.group.mock.GroupEventsServiceMock
import base.entity.kv.KvFactoryService
import base.entity.question.mock.QuestionServiceMock
import base.entity.question.model.{ QuestionsResponseModel, QuestionsModel }

import scala.concurrent.Future

/**
 * Responsible for testing the logic and side effects of the Impl
 * (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class QuestionsCommandServiceImplTest extends CommandServiceImplTest {

  val service = new QuestionsCommandServiceImpl()

  private val body = "a message"
  private val groupId = RandomService().uuid

  private val error = ApiError("test")

  private val questionsMock = new QuestionServiceMock()

  private implicit val pipeline = KvFactoryService().pipeline
  private implicit val authCtx = AuthContextDataFactory.userAuth
  private implicit val model = QuestionsModel(groupId)

  override def beforeAll() {
    super.beforeAll()
    Services.register(questionsMock)
  }

  private def command(implicit input: QuestionsModel) = new service.QuestionsCommand(input)

  test("without perms") {
    assertPermException(authCtx => {
      service.execute(model)(authCtx)
    })
  }

  test("success") {
    val response = QuestionsResponseModel(groupId, List())
    assert(service.innerExecute(model).await() == Right(response))
  }

  test("questions get failed") {
    val unregister = TestServices.register(new QuestionServiceMock(getResult = Future.successful(Left(error))))
    assert(command.questionsGet().await() == Left(error))
    unregister()
  }

}
