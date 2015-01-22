/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 11:38 AM
 */

package base.entity.question.impl

import java.util.UUID

import base.common.random.RandomService
import base.common.service.{ CommonService, ServiceImpl }
import base.entity.auth.context.AuthContext
import base.entity.event.EventTypes
import base.entity.event.model.EventModel
import base.entity.group.kv._
import base.entity.kv.Key._
import base.entity.kv.{ KeyId, KvFactoryService }
import base.entity.question.QuestionSides.QuestionSide
import base.entity.question.kv.QuestionsKeyService
import base.entity.question.model.{ AnswerModel, QuestionModel }
import base.entity.question.{ QuestionDef, QuestionIdComposite, QuestionService, QuestionSides }
import base.entity.service.CrudImplicits
import redis.client.RedisException

import scala.concurrent.{ Await, Future }

/**
 * {{ Describe the high level purpose of QuestionServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class QuestionServiceImpl(questions: Iterable[QuestionDef],
                          questionCount: Int) extends ServiceImpl with QuestionService {

  private val questionMap = questions.map(q => q.id -> q).toMap
  private val questionKey = QuestionsKeyService().make(KeyId("standard"))(KvFactoryService().pipeline)

  Await.ready(init(), CommonService().defaultDuration)

  private[impl] def init() = {
    implicit val p = KvFactoryService().pipeline
    val compositeIds_a = questions.map(q => compositeId(q.id, QuestionSides.SIDE_A))
    val compositeIds_b = questions.collect { case q if q.b.isDefined => compositeId(q.id, QuestionSides.SIDE_B) }
    val compositeIds = compositeIds_a.toSet ++ compositeIds_b
    questionKey.add(compositeIds.toSeq: _*)
  }

  private[impl] def compositeId(questionId: UUID, side: QuestionSide, inverse: Boolean = false) = {
    assert(questionMap.contains(questionId))
    QuestionIdComposite(questionMap(questionId), side, inverse)
  }

  def getQuestions(groupId: UUID)(implicit p: Pipeline, authCtx: AuthContext) =
    new GetQuestionsMethod(groupId).execute()

  def answer(input: AnswerModel)(implicit p: Pipeline, authCtx: AuthContext) =
    new AnswerMethod(input).execute()

  /**
   * - diffstore standard questions with answered questions
   * - get $n random questions from stored set
   * - delete stored set
   * - return questions
   */
  private[impl] class GetQuestionsMethod(groupId: UUID)(implicit p: Pipeline, authCtx: AuthContext)
      extends CrudImplicits[List[QuestionModel]] {

    def execute() = {
      val temp = GroupUserQuestionsTempKeyService().make(groupId, authCtx.userId)
      val answered = GroupUserQuestionsKeyService().make(groupId, authCtx.userId)
      groupUserQuestionsTempDiffStore(temp, answered)
    }

    def groupUserQuestionsTempDiffStore(temp: GroupUserQuestionsTempKey, answered: GroupUserQuestionsKey) =
      temp.diffStore(questionKey, answered).flatMap {
        case n if n <= 0 => Future.successful(Right(List()))
        case n           => groupUserQuestionTempRand(temp)
      }

    def groupUserQuestionTempRand(temp: GroupUserQuestionsTempKey) =
      temp.rand(questionCount).flatMap { compositeIds =>
        groupUserQuestionTempDel(temp, compositeIds)
      }

    def groupUserQuestionTempDel(temp: GroupUserQuestionsTempKey, compositeIds: Iterable[QuestionIdComposite]) =
      temp.del().map {
        case false => throw new RedisException(s"failed to delete $temp")
        case true  => Right(compositeIds.map(makeQuestionModelFromCompositeId).toList)
      }

    def makeQuestionModelFromCompositeId(compositeId: QuestionIdComposite) = {
      QuestionModel(questionMap(compositeId.questionId), compositeId.side)
    }

  }

  /**
   * - add question to answered set
   * - if question response is true:
   *    - add question to answered yes set
   *    - get other group member user ids
   *    - check if inverseId is in any other user answered yes set
   *    - create match events in group list
   *    - notify other group members of match events (???)
   *    - return match events for all matches
   */
  private[impl] class AnswerMethod(input: AnswerModel)(implicit p: Pipeline, authCtx: AuthContext)
      extends CrudImplicits[List[EventModel]] {

    def execute() = {
      val key = GroupUserQuestionsKeyService().make(input.groupId, authCtx.userId)
      groupUserQuestionAdd(key)
    }

    def groupUserQuestionAdd(key: GroupUserQuestionsKey) = {
      val id = compositeId(input.questionId, input.side)
      key.add(id).flatMap { added =>
        questionResponse(id)
      }
    }

    def questionResponse(id: QuestionIdComposite) =
      input.response match {
        case true  => groupUserQuestionYesAdd(id, GroupUserQuestionsYesKeyService().make(input.groupId, authCtx.userId))
        case false => Future.successful(Right(List()))
      }

    def groupUserQuestionYesAdd(id: QuestionIdComposite, key: GroupUserQuestionsYesKey) =
      key.add(id).flatMap { added =>
        groupUsersGet(GroupUsersKeyService().make(KeyId(input.groupId)))
      }

    def groupUsersGet(key: GroupUsersKey) =
      key.members().flatMap { allUserIds =>
        val otherUserIds = allUserIds.collect {
          case userId if userId != authCtx.userId => userId
        }
        groupUsersQuestionYesGet(otherUserIds)
      }

    def groupUsersQuestionYesGet(userIds: Iterable[UUID]) = {
      val inverseId = compositeId(input.questionId, input.side, inverse = true)
      val futures = userIds.map { userId =>
        groupUserQuestionYesGet(userId, inverseId, GroupUserQuestionsYesKeyService().make(input.groupId, userId))
      }
      val matches = Future.sequence(futures).map(_.collect {
        case (userId, res) if res => userId
      })
      matches.map { userIds =>
        buildEvents(userIds)
      }
    }

    def groupUserQuestionYesGet(userId: UUID, inverseId: QuestionIdComposite, key: GroupUserQuestionsYesKey) =
      key.isMember(inverseId).map { res =>
        userId -> res
      }

    def buildEvents(userIds: Iterable[UUID]) = {
      val matches = userIds.map { userId =>
        val eventId = RandomService().uuid
        val body = questionMap(input.questionId) + " is a match"
        EventModel(eventId, input.groupId, userId = None, EventTypes.MATCH, body)
      }
      Right(matches.toList)
    }

  }

}
