/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:13 PM
 */

package base.entity.question.impl

import java.util.UUID

import base.common.random.RandomService
import base.common.random.mock.RandomServiceMock
import base.common.service.Services
import base.common.time.mock.TimeServiceConstantMock
import base.entity.auth.context.ChannelContextDataFactory
import base.entity.event.EventTypes._
import base.entity.event.model.impl.EventModelImpl
import base.entity.group.kv._
import base.entity.kv.KvTest
import base.entity.question.QuestionSides._
import base.entity.question.impl.QuestionServiceImpl.Errors
import base.entity.question.kv.QuestionKey
import base.entity.question.model.{ AnswerModel, QuestionModel }
import base.entity.question.{ QuestionDef, QuestionIdComposite }
import base.entity.service.EntityServiceTest
import scredis.exceptions.RedisException

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of QuestionServiceImplTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class QuestionServiceImplTest extends EntityServiceTest with KvTest {

  private val totalSides = 6
  private val groupCount = 2
  private val questions = List(
    ("65b76c8e-a9b3-4eda-b6dc-ebee6ef78a04", "Doin' it with the lights off", None),
    ("2c75851f-1a87-40ab-9f66-14766fa12c6c", "Liberal use of chocolate sauce", None),
    ("a8d08d2d-d914-4e7b-8ff4-6a9dde02002c", "Giving buttsex", Option("Receiving buttsex")),
    ("543c57e3-54ba-4d9b-8ed3-c8f2e394b18d", "Dominating", Option("Being dominated"))
  ).map(q => QuestionDef(UUID.fromString(q._1), q._2, q._3))
  private val groupQuestion = QuestionDef(RandomService().uuid, "user generated question")

  val service = new QuestionServiceImpl(questions, totalSides * 2, groupCount)

  private val randomMock = new RandomServiceMock()

  private implicit val channelCtx = ChannelContextDataFactory.userAuth()

  override def beforeAll() {
    super.beforeAll()
    Services.register(randomMock)
    Services.register(TimeServiceConstantMock)
  }

  override def beforeEach() {
    super.beforeEach()
    assert(service.init().await() == totalSides)
  }

  test("questions - success - no group questions") {
    val groupId = RandomService().uuid
    val questionModels = questions.map(QuestionModel(_, SIDE_A)) ++
      questions.collect {
        case q if q.b.isDefined => QuestionModel(q, SIDE_B)
      }

    service.getQuestions(groupId, authCtx.userId).await() match {
      case Right(models) => assert(models.toSet == questionModels.toSet)
      case Left(e)       => fail()
    }
  }

  test("questions - success - yes group questions") {
    val groupId = RandomService().uuid

    val groupQuestionsKey = make[GroupQuestionsKey](groupId)
    assert(groupQuestionsKey.add(QuestionIdComposite(groupQuestion, SIDE_A)).await() == 1L)

    val questionKey = make[QuestionKey](groupQuestion.id)
    questionKey.createDef(groupQuestion.a, groupQuestion.b, authCtx.userId).await()

    val questionModels = List(QuestionModel(groupQuestion, SIDE_A)) ++
      questions.map(QuestionModel(_, SIDE_A)) ++
      questions.collect {
        case q if q.b.isDefined => QuestionModel(q, SIDE_B)
      }

    service.getQuestions(groupId, authCtx.userId).await() match {
      case Right(models) =>
        debug(models.sortBy(_.id).toString())
        debug(questionModels.sortBy(_.id).toString())
        assert(models.toSet == questionModels.toSet)
      case Left(e) => fail()
    }
  }

  test("questions - failed to delete") {
    val groupId = RandomService().uuid
    val key = mock[GroupUserQuestionsTempKey]
    key.del _ expects () returning Future.successful(false)

    val method = new service.GetQuestionsMethod(groupId, authCtx.userId)
    intercept[RedisException] {
      method.groupUserQuestionTempDel(key, Iterable[QuestionIdComposite](), method.makeStandardQuestionModel).await()
    }
  }

  test("answer - standard question - success") {
    val questionDef = questions(3)
    val groupId = RandomService().uuid
    val otherUserId = RandomService().uuid
    val questionResponse = true
    val side = SIDE_A
    val composite = QuestionIdComposite(questionDef, side)

    val groupUsersKey = make[GroupUsersKey](groupId)
    assert(groupUsersKey.add(otherUserId).await() == 1L)
    val otherUserYesKey = make[GroupUserQuestionsYesKey](groupId, otherUserId)
    assert(otherUserYesKey.add(QuestionIdComposite(questionDef, SIDE_B)).await() == 1)

    val model = AnswerModel(questionDef.id, groupId, side, questionResponse)
    val eventId = randomMock.nextUuid()
    val response = EventModelImpl(eventId, groupId, None, MATCH, questions(3) + " is a match")

    debugAssert(service.answer(model).await(), Right(List(response)))

    val userQuestionsKey = make[GroupUserQuestionsKey](groupId, authCtx.userId)
    assert(userQuestionsKey.isMember(composite).await())
    val userQuestionsYesKey = make[GroupUserQuestionsYesKey](groupId, authCtx.userId)
    assert(userQuestionsYesKey.isMember(composite).await())
  }

  test("answer - user question - success") {
    val questionId = RandomService().uuid
    val questionDef = QuestionDef(questionId, "side a", Option("side b"))
    val groupId = RandomService().uuid
    val otherUserId = RandomService().uuid
    val questionResponse = true
    val side = SIDE_A
    val composite = QuestionIdComposite(questionDef, side)

    val groupUsersKey = make[GroupUsersKey](groupId)
    assert(groupUsersKey.add(otherUserId).await() == 1L)
    val otherUserYesKey = make[GroupUserQuestionsYesKey](groupId, otherUserId)
    assert(otherUserYesKey.add(QuestionIdComposite(questionId, SIDE_B)).await() == 1)
    val questionKey = make[QuestionKey](questionId)
    questionKey.createDef(questionDef.a, questionDef.b, otherUserId).await()

    val model = AnswerModel(questionId, groupId, side, questionResponse)
    val eventId = randomMock.nextUuid()
    val response = EventModelImpl(eventId, groupId, None, MATCH, questionDef + " is a match")

    debugAssert(service.answer(model).await(), Right(List(response)))

    val userQuestionsKey = make[GroupUserQuestionsKey](groupId, authCtx.userId)
    assert(userQuestionsKey.isMember(composite).await())
    val userQuestionsYesKey = make[GroupUserQuestionsYesKey](groupId, authCtx.userId)
    assert(userQuestionsYesKey.isMember(composite).await())
  }

  test("answer - question response false") {
    val questionId = questions(1).id
    val groupId = RandomService().uuid
    val side = SIDE_A
    val response = false
    val input = AnswerModel(questionId, groupId, side, response)
    val method = new service.AnswerMethod(input)
    val id = QuestionIdComposite(questions(1), side)
    assert(method.questionResponse(id).await() == Right(List()))
  }

  test("answer - already answered") {
    val key = mock[GroupUserQuestionsKey]
    key.add _ expects * returning Future.successful(0L)
    val method = new service.AnswerMethod(AnswerModel(questions(1).id, RandomService().uuid, SIDE_A, response = false))
    assert(method.groupUserQuestionAdd(key).await() == Errors.alreadyAnswered.await())
  }

  test("answer - question doesn't exist") {
    val key = make[QuestionKey](RandomService().uuid)
    val method = new service.AnswerMethod(AnswerModel(questions(1).id, RandomService().uuid, SIDE_A, response = false))
    intercept[RedisException] {
      method.questionGet(key, Set()).await()
    }
  }

}
