/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/21/15 10:33 PM
 */

package base.entity.question.impl

import java.util.UUID

import base.common.random.RandomService
import base.common.random.mock.RandomServiceMock
import base.common.service.Services
import base.common.time.mock.TimeServiceConstantMock
import base.entity.auth.context.AuthContextDataFactory
import base.entity.event.EventTypes._
import base.entity.event.model.EventModel
import base.entity.group.kv.{ GroupUserQuestionsTempKey, GroupUserQuestionsYesKeyService, GroupUsersKeyService }
import base.entity.kv.{ KeyId, KvTest }
import base.entity.question.QuestionSides._
import base.entity.question.model.{ AnswerModel, QuestionModel }
import base.entity.question.{ QuestionDef, QuestionIdComposite }
import base.entity.service.EntityServiceTest
import redis.client.RedisException

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of QuestionServiceImplTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class QuestionServiceImplTest extends EntityServiceTest with KvTest {

  private val totalSides = 6
  private val questions = List(
    ("65b76c8e-a9b3-4eda-b6dc-ebee6ef78a04", "Doin' it with the lights off", None),
    ("2c75851f-1a87-40ab-9f66-14766fa12c6c", "Liberal use of chocolate sauce", None),
    ("a8d08d2d-d914-4e7b-8ff4-6a9dde02002c", "Giving buttsex", Option("Receiving buttsex")),
    ("543c57e3-54ba-4d9b-8ed3-c8f2e394b18d", "Dominating", Option("Being dominated"))
  ).map(q => QuestionDef(UUID.fromString(q._1), q._2, q._3))

  val service = new QuestionServiceImpl(questions, totalSides)

  private val randomMock = new RandomServiceMock()

  private implicit val authCtx = AuthContextDataFactory.userAuth

  override def beforeAll() {
    super.beforeAll()
    Services.register(randomMock)
    Services.register(TimeServiceConstantMock)
  }

  override def beforeEach() {
    super.beforeEach()
    assert(service.init().await() == totalSides)
  }

  test("questions - success") {
    val groupId = RandomService().uuid
    val questionModels = questions.map(QuestionModel(_, SIDE_A)) ++
      questions.collect {
        case q if q.b.isDefined => QuestionModel(q, SIDE_B)
      }

    service.getQuestions(groupId).await() match {
      case Right(models) => assert(models.toSet == questionModels.toSet)
    }
  }

  test("questions - failed to delete") {
    val groupId = RandomService().uuid
    val key = mock[GroupUserQuestionsTempKey]
    key.del _ expects () returning Future.successful(false)
    key.token _ expects () returning ""

    val method = new service.GetQuestionsMethod(groupId)
    intercept[RedisException] {
      method.groupUserQuestionTempDel(key, Iterable[String]()).await()
    }
  }

  test("answer - success") {
    val questionId = questions(3).id
    val groupId = RandomService().uuid
    val userId = RandomService().uuid
    val questionResponse = true

    val usersKey = GroupUsersKeyService().make(KeyId(groupId))
    assert(usersKey.add(userId).await() == 1)
    val userYesKey = GroupUserQuestionsYesKeyService().make(groupId, userId)
    assert(userYesKey.add(questionId.toString + SIDE_B).await() == 1)

    val model = AnswerModel(questionId, groupId, SIDE_A, questionResponse)
    val eventId = randomMock.nextUuid()
    val response = EventModel(eventId, groupId, None, MATCH, questions(3) + " is a match")

    val actual = service.answer(model).await()
    val expected = Right(List(response))

    debug(actual.toString)
    debug(expected.toString)

    assert(actual == expected)
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

}
