/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 1:16 PM
 */

package base.entity.question.impl

import base.common.random.RandomService
import base.common.random.mock.RandomServiceMock
import base.common.service.Services
import base.common.time.mock.TimeServiceConstantMock
import base.entity.auth.context.ChannelContextDataFactory
import base.entity.command.impl.CommandServiceImplTest
import base.entity.group.kv.{ GroupQuestionsKey, GroupUsersKey }
import base.entity.question.impl.CreateQuestionCommandServiceImpl.Errors
import base.entity.question.kv.{ QuestionKey, QuestionsKey }
import base.entity.question.model.{ CreateQuestionModel, CreateQuestionResponseModel }
import base.entity.question.{ QuestionDef, QuestionIdComposite, QuestionSides }
import base.entity.user.kv.UserQuestionsKey

import scala.concurrent.Future

/**
 * Responsible for testing the logic and side effects of the Impl
 * (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class CreateQuestionCommandServiceImplTest extends CommandServiceImplTest {

  private val maxQuestions = 1000

  val service = new CreateQuestionCommandServiceImpl(maxQuestions)

  private val randomMock = new RandomServiceMock()

  private val groupId = RandomService().uuid

  private val sideA = "question side a"
  private val sideB = "question side b"

  private implicit val channelCtx = ChannelContextDataFactory.userAuth
  private implicit val model = CreateQuestionModel(groupId, sideA, Option(sideB))

  private def command(implicit input: CreateQuestionModel) = new service.CreateQuestionCommand(input)

  override def beforeAll() {
    super.beforeAll()
    Services.register(randomMock)
    Services.register(TimeServiceConstantMock)
  }

  test("without perms") {
    assertPermException(channelCtx => {
      service.execute(model)(channelCtx)
    })
  }

  test("success") {
    val groupUsersKey = make[GroupUsersKey](groupId)
    assert(groupUsersKey.add(authCtx.userId).await() == 1L)

    val questionId = randomMock.nextUuid()
    val response = CreateQuestionResponseModel(groupId, questionId)
    assert(service.innerExecute(model).await() == Right(response))

    val questionKey = make[QuestionKey](questionId)
    debug(questionKey.getCreatorId.await().toString)
    assert(questionKey.getCreatorId.await().contains(authCtx.userId))
    assert(questionKey.getQuestionDef(questionId).await() == QuestionDef(questionId, sideA, Option(sideB)))

    val composites = Set(
      QuestionIdComposite(questionId, QuestionSides.SIDE_A),
      QuestionIdComposite(questionId, QuestionSides.SIDE_B))

    val groupQuestionsKey = make[GroupQuestionsKey](groupId)
    assert(groupQuestionsKey.members.await() == composites)

    val userQuestionsKey = make[UserQuestionsKey](authCtx.userId)
    assert(userQuestionsKey.members.await() == composites)

    val allQuestionsKey = make[QuestionsKey](QuestionServiceImpl.userQuestionsKey)
    assert(allQuestionsKey.members.await() == composites)
  }

  test("fail - user is not member") {
    val key = mock[GroupUsersKey]
    key.isMember _ expects * returning Future.successful(false)
    assert(command.groupUsersIsMember(key).await() == Errors.userNotGroupMember.await())
  }

  test("fail - user has created too many questions") {
    val key = mock[UserQuestionsKey]
    key.card _ expects () returning Future.successful(maxQuestions + 1)
    assert(command.userQuestionCount(key).await() == Errors.userCreatedTooManyQuestions.await())
  }

  test("fail - group questions add failed") {
    val questions = Seq(QuestionIdComposite(groupId, QuestionSides.SIDE_A))
    val key = mock[GroupQuestionsKey]
    key.add _ expects * returning Future.successful(0L)
    assert(command.groupQuestionsAdd(questions, key).await() == Errors.groupQuestionsAddFailed.await())
  }

  test("fail - user questions add failed") {
    val questions = Seq(QuestionIdComposite(groupId, QuestionSides.SIDE_A))
    val key = mock[UserQuestionsKey]
    key.add _ expects * returning Future.successful(0L)
    assert(command.userQuestionsAdd(questions, key).await() == Errors.userQuestionsAddFailed.await())
  }

  test("fail - all questions add failed") {
    val questions = Seq(QuestionIdComposite(groupId, QuestionSides.SIDE_A))
    val key = mock[QuestionsKey]
    key.add _ expects * returning Future.successful(0L)
    assert(command.allUserQuestionsAdd(questions, key).await() == Errors.allQuestionsAddFailed.await())
  }

}
