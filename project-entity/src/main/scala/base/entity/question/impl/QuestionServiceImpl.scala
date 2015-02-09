/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 5:28 PM
 */

package base.entity.question.impl

import java.util.UUID

import base.common.random.RandomService
import base.common.service.{CommonService, ServiceImpl}
import base.entity.api.ApiErrorCodes._
import base.entity.auth.context.ChannelContext
import base.entity.event.EventTypes
import base.entity.event.model.EventModel
import base.entity.event.model.impl.EventModelImpl
import base.entity.group.kv._
import base.entity.kv.Key._
import base.entity.kv.{KvFactoryService, MakeKey}
import base.entity.logging.AuthLoggable
import base.entity.question.QuestionSides.QuestionSide
import base.entity.question.impl.QuestionServiceImpl.Errors
import base.entity.question.kv.QuestionsKey
import base.entity.question.model.{AnswerModel, QuestionModel}
import base.entity.question.{QuestionDef, QuestionIdComposite, QuestionService, QuestionSides}
import base.entity.service.{CrudErrorImplicits, CrudImplicits}
import redis.client.RedisException

import scala.concurrent.{Await, Future}

/**
 * {{ Describe the high level purpose of QuestionServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class QuestionServiceImpl(questions: Iterable[QuestionDef],
                          questionCount: Int) extends ServiceImpl with QuestionService with MakeKey with AuthLoggable {

  private val questionMap = questions.map(q => q.id -> q).toMap
  private val questionKey = make[QuestionsKey]("standard")

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

  def getQuestions(groupId: UUID, userId: UUID)(implicit p: Pipeline, channelCtx: ChannelContext) =
    new GetQuestionsMethod(groupId, userId: UUID).execute()

  def answer(input: AnswerModel)(implicit p: Pipeline, channelCtx: ChannelContext) =
    new AnswerMethod(input).execute()

  /**
   * - diffstore standard questions with answered questions
   * - get $n random questions from stored set
   * - delete stored set
   * - return questions
   */
  private[impl] class GetQuestionsMethod(groupId: UUID, userId: UUID)(implicit p: Pipeline, channelCtx: ChannelContext)
      extends CrudImplicits[List[QuestionModel]] {

    def execute() = {
      val temp = make[GroupUserQuestionsTempKey](groupId, userId)
      val answered = make[GroupUserQuestionsKey](groupId, userId)
      groupUserQuestionsTempDiffStore(temp, answered)
    }

    def groupUserQuestionsTempDiffStore(temp: GroupUserQuestionsTempKey, answered: GroupUserQuestionsKey) =
      temp.diffStore(questionKey, answered).flatMap {
        case n if n <= 0 => Future.successful(Right(List()))
        case n           => groupUserQuestionTempRand(temp)
      }

    def groupUserQuestionTempRand(temp: GroupUserQuestionsTempKey) =
      temp.randMembers(questionCount).flatMap { compositeIds =>
        groupUserQuestionTempDel(temp, compositeIds)
      }

    def groupUserQuestionTempDel(temp: GroupUserQuestionsTempKey, compositeIds: Iterable[QuestionIdComposite]) =
      temp.del().map {
        case true  => Right(compositeIds.map(makeQuestionModelFromCompositeId).toList)
        case false => throw new RedisException(s"failed to delete $temp")
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
  private[impl] class AnswerMethod(input: AnswerModel)(implicit p: Pipeline, channelCtx: ChannelContext)
      extends CrudImplicits[List[EventModel]] {

    def execute() = {
      val key = make[GroupUserQuestionsKey](input.groupId, channelCtx.authCtx.userId)
      groupUserQuestionAdd(key)
    }

    def groupUserQuestionAdd(key: GroupUserQuestionsKey): Response = {
      val id = compositeId(input.questionId, input.side)
      key.add(id).flatMap {
        case 1L => questionResponse(id)
        case _ => Errors.alreadyAnswered
      }
    }

    def questionResponse(id: QuestionIdComposite) =
      input.response match {
        case true =>
          groupUserQuestionYesAdd(id, make[GroupUserQuestionsYesKey](input.groupId, channelCtx.authCtx.userId))
        case false =>
          Future.successful(Right(List()))
      }

    def groupUserQuestionYesAdd(id: QuestionIdComposite, key: GroupUserQuestionsYesKey) =
      key.add(id).flatMap { added =>
        groupUsersGet(make[GroupUsersKey](input.groupId))
      }

    def groupUsersGet(key: GroupUsersKey) =
      key.members.flatMap { allUserIds =>
        val otherUserIds = allUserIds.collect {
          case userId if userId != channelCtx.authCtx.userId => userId
        }
        groupUsersQuestionYesGet(otherUserIds)
      }

    def groupUsersQuestionYesGet(userIds: Iterable[UUID]) = {
      val inverseId = compositeId(input.questionId, input.side, inverse = true)
      val futures = userIds.map { userId =>
        groupUserQuestionYesGet(userId, inverseId, make[GroupUserQuestionsYesKey](input.groupId, userId))
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
        debug("found match for %s", userId)
        val eventId = RandomService().uuid
        val body = questionMap(input.questionId) + " is a match"
        EventModelImpl(eventId, input.groupId, userId = None, EventTypes.MATCH, body)
      }
      Right(matches.toList)
    }

  }

}

object QuestionServiceImpl {

  object Errors extends CrudErrorImplicits[List[EventModel]] {

    val alreadyAnswered: Response = ("This question has already been answered.", ANSWERED_ALREADY)

  }

}
