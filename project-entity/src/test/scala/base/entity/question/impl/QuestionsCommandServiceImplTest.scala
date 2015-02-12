/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 10:16 PM
 */

package base.entity.question.impl

import java.util.UUID

import base.common.random.RandomService
import base.common.service.TestServices
import base.entity.auth.context.{ ChannelContext, ChannelContextDataFactory }
import base.entity.command.impl.CommandServiceImplTest
import base.entity.error.ApiErrorService
import base.entity.group.kv.GroupUsersKey
import base.entity.question.QuestionService
import base.entity.question.model.{ QuestionsModel, QuestionsResponseModel }

import scala.concurrent.Future

/**
 * Responsible for testing the logic and side effects of the Impl
 * (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class QuestionsCommandServiceImplTest extends CommandServiceImplTest {

  val service = new QuestionsCommandServiceImpl()

  private val groupId = RandomService().uuid

  private val error = ApiErrorService().badRequest("test")

  private implicit val channelCtx = ChannelContextDataFactory.userAuth
  private implicit val model = QuestionsModel(groupId)

  private def command(implicit input: QuestionsModel) = new service.QuestionsCommand(input)

  test("without perms") {
    assertPermException(channelCtx => {
      service.execute(model)(channelCtx)
    })
  }

  test("success") {
    val groupUsersKey = make[GroupUsersKey](groupId)
    assert(groupUsersKey.add(authCtx.userId).await() == 1L)
    val questionService = mock[QuestionService]
    (questionService.getQuestions(_: UUID, _: UUID)(_: ChannelContext)) expects
      (*, *, *) returning Future.successful(Right(List()))
    val unregister = TestServices.register(questionService)
    val response = QuestionsResponseModel(groupId, List())
    assert(service.innerExecute(model).await() == Right(response))
    unregister()
  }

  test("questions get failed") {
    val questionService = mock[QuestionService]
    (questionService.getQuestions(_: UUID, _: UUID)(_: ChannelContext)) expects
      (*, *, *) returning Future.successful(Left(error))
    val unregister = TestServices.register(questionService)
    assert(command.questionsGet().await() == Left(error))
    unregister()
  }

}
