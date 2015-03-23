/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/22/15 5:44 PM
 */

package base.entity.question.impl

import java.util.UUID

import base.common.random.RandomService
import base.common.service.{ CommonService, ServiceImpl }
import base.entity.api.ApiErrorCodes._
import base.entity.auth.context.ChannelContext
import base.entity.event.EventTypes
import base.entity.event.model.EventModel
import base.entity.event.model.impl.EventModelImpl
import base.entity.group.kv._
import base.entity.kv.MakeKey
import base.entity.logging.AuthLoggable
import base.entity.question.impl.QuestionServiceImpl.Errors
import base.entity.question.kv.{ QuestionKey, QuestionsKey }
import base.entity.question.model.{ AnswerModel, QuestionModel }
import base.entity.question.{ QuestionDef, QuestionIdComposite, QuestionService, QuestionSides }
import base.entity.service.{ CrudErrorImplicits, CrudImplicits }
import scredis.exceptions.RedisException
import scredis.keys.SetKey

import scala.concurrent.{ Await, Future }

/**
 * {{ Describe the high level purpose of QuestionServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class QuestionServiceImpl(questions: Iterable[QuestionDef],
                          questionCount: Int,
                          groupQuestionCount: Int)
    extends ServiceImpl
    with QuestionService
    with MakeKey
    with AuthLoggable {

  private val questionsMap = questions.map(q => q.id -> q).toMap
  private val questionsKey = make[QuestionsKey]("standard")

  Await.ready(init(), CommonService().defaultDuration)

  private[impl] def init() = {
    val compositeIds_a = questions.map { q =>
      QuestionIdComposite(q.id, QuestionSides.SIDE_A)
    }
    val compositeIds_b = questions.collect {
      case q if q.b.isDefined =>
        QuestionIdComposite(q.id, QuestionSides.SIDE_B)
    }
    val compositeIds = compositeIds_a.toSet ++ compositeIds_b
    questionsKey.add(compositeIds.toSeq: _*)
  }

  def getQuestions(groupId: UUID, userId: UUID)(implicit channelCtx: ChannelContext) =
    new GetQuestionsMethod(groupId, userId: UUID).execute()

  def answer(input: AnswerModel)(implicit channelCtx: ChannelContext) =
    new AnswerMethod(input).execute()

  /**
   * - diffstore group user questions with answered questions
   *   - get $n random questions from stored set
   *   - delete stored set
   * - diffstore standard questions with answered questions
   *   - get $n random questions from stored set
   *   - delete stored set
   * - return combined questions
   */
  private[impl] class GetQuestionsMethod(groupId: UUID, userId: UUID)(implicit channelCtx: ChannelContext)
      extends CrudImplicits[List[QuestionModel]] {

    def execute() = {
      diffStoreGroupQuestions()
    }

    def diffStoreGroupQuestions() = {
      diffStoreQuestions(make[GroupQuestionsKey](groupId), groupQuestionCount, makeGroupQuestionModel) flatMap {
        case Right(groupQuestions) => diffStoreStandardQuestions(groupQuestions)
        case Left(error)           => error
      }
    }

    def diffStoreStandardQuestions(groupQuestions: Iterable[QuestionModel]) = {
      val count = Math.max(0, questionCount - groupQuestions.size)
      diffStoreQuestions(questionsKey, count, makeStandardQuestionModel) map {
        case Right(standardQuestions) => Right(combineQuestions(standardQuestions, groupQuestions).toList)
        case error                    => error
      }
    }

    def combineQuestions(questionSets: Iterable[QuestionModel]*) = {
      RandomService().random.shuffle(questionSets.reduce(_ ++ _))
    }

    def diffStoreQuestions(questionsKey: SetKey[_, _, QuestionIdComposite],
                           questionCount: Int,
                           getModel: (QuestionIdComposite) => Future[QuestionModel]): Response = {
      val temp = make[GroupUserQuestionsTempKey](groupId, userId)
      val answered = make[GroupUserQuestionsKey](groupId, userId)
      groupUserQuestionsTempDiffStore(questionsKey, temp, answered, getModel)
    }

    def groupUserQuestionsTempDiffStore(questionsKey: SetKey[_, _, QuestionIdComposite],
                                        temp: GroupUserQuestionsTempKey,
                                        answered: GroupUserQuestionsKey,
                                        getModel: (QuestionIdComposite) => Future[QuestionModel]) =
      temp.diffStore(questionsKey, answered) flatMap {
        case n if n <= 0 => Future.successful(Right(List()))
        case n           => groupUserQuestionTempRand(temp, getModel)
      }

    def groupUserQuestionTempRand(temp: GroupUserQuestionsTempKey,
                                  getModel: (QuestionIdComposite) => Future[QuestionModel]) =
      temp.randMembers(questionCount) flatMap { compositeIds =>
        groupUserQuestionTempDel(temp, compositeIds, getModel)
      }

    def groupUserQuestionTempDel(temp: GroupUserQuestionsTempKey,
                                 compositeIds: Iterable[QuestionIdComposite],
                                 getModel: (QuestionIdComposite) => Future[QuestionModel]) =
      temp.del() flatMap {
        case true  => Future.sequence(compositeIds.map(getModel).toList).map(Right.apply)
        case false => throw new RedisException(s"failed to delete $temp") {}
      }

    def makeStandardQuestionModel(compositeId: QuestionIdComposite) = {
      Future.successful(QuestionModel(questionsMap(compositeId.questionId), compositeId.side))
    }

    def makeGroupQuestionModel(compositeId: QuestionIdComposite) = {
      questionGet(make[QuestionKey](compositeId.questionId), compositeId)
    }

    def questionGet(key: QuestionKey, compositeId: QuestionIdComposite) =
      key.getQuestionDef(compositeId.questionId) map { questionDef =>
        QuestionModel(questionDef, compositeId.side)
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
  private[impl] class AnswerMethod(input: AnswerModel)(implicit channelCtx: ChannelContext)
      extends CrudImplicits[List[EventModel]] {

    def execute() = {
      val key = make[GroupUserQuestionsKey](input.groupId, channelCtx.authCtx.userId)
      groupUserQuestionAdd(key)
    }

    def groupUserQuestionAdd(key: GroupUserQuestionsKey): Response = {
      val id = QuestionIdComposite(input.questionId, input.side)
      key.add(id).flatMap {
        case 1L => questionResponse(id)
        case _  => Errors.alreadyAnswered
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
        standardQuestionGet(otherUserIds)
      }

    def standardQuestionGet(userIds: Iterable[UUID]) = questionsMap.get(input.questionId) match {
      case Some(questionDef) => groupUsersQuestionYesGet(questionDef, userIds)
      case None              => questionGet(make[QuestionKey](input.questionId), userIds)
    }

    def questionGet(key: QuestionKey, userIds: Iterable[UUID]) =
      key.getQuestionDef(input.questionId) flatMap { questionDef =>
        groupUsersQuestionYesGet(questionDef, userIds)
      }

    def groupUsersQuestionYesGet(questionDef: QuestionDef, userIds: Iterable[UUID]) = {
      val inverseId = QuestionIdComposite(questionDef, input.side, inverse = true)
      val futures = userIds.map { userId =>
        groupUserQuestionYesGet(userId, inverseId, make[GroupUserQuestionsYesKey](input.groupId, userId))
      }
      val matches = Future.sequence(futures).map(_.collect {
        case (userId, res) if res => userId
      })
      matches.map { userIds =>
        buildEvents(questionDef, userIds)
      }
    }

    def groupUserQuestionYesGet(userId: UUID, inverseId: QuestionIdComposite, key: GroupUserQuestionsYesKey) =
      key.isMember(inverseId).map { res =>
        userId -> res
      }

    def buildEvents(questionDef: QuestionDef, userIds: Iterable[UUID]) = {
      val matches = userIds.map { userId =>
        debug("found match for %s", userId)
        val eventId = RandomService().uuid
        val body = questionDef + " is a match"
        EventModelImpl(eventId, group = None, input.groupId, userId = None, EventTypes.MATCH, body)
      }
      Right(matches.toList)
    }

  }

}

object QuestionServiceImpl {

  val userQuestionsKey = "user"
  val standardQuestionsKey = "standard"

  object Errors extends CrudErrorImplicits[List[EventModel]] {

    val alreadyAnswered: Response = ("This question has already been answered.", ANSWERED_ALREADY)

  }

}
