/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:13 PM
 */

package base.entity.question.impl

import java.util.UUID

import base.common.random.RandomService
import base.entity.auth.context.ChannelContext
import base.entity.command.Command
import base.entity.command.impl.CommandServiceImpl
import base.entity.group.kv.GroupQuestionsKey
import base.entity.question.QuestionSides._
import base.entity.question.impl.CreateQuestionCommandServiceImpl.Errors
import base.entity.question.kv.{ QuestionKey, QuestionsKey }
import base.entity.question.model.{ CreateQuestionModel, CreateQuestionResponseModel }
import base.entity.question.{ CreateQuestionCommandService, QuestionIdComposite }
import base.entity.service.CrudErrorImplicits
import base.entity.user.kv.UserQuestionsKey

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class CreateQuestionCommandServiceImpl(maxUserQuestions: Int)
    extends CommandServiceImpl[CreateQuestionModel, CreateQuestionResponseModel]
    with CreateQuestionCommandService {

  override protected val responseManifest = Option(manifest[CreateQuestionResponseModel])

  def innerExecute(input: CreateQuestionModel)(implicit channelCtx: ChannelContext) = {
    new CreateQuestionCommand(input).execute()
  }

  /**
   * - check if member of group
   * - create question
   * - add to group
   * - add to user
   * - add to all
   */
  private[impl] class CreateQuestionCommand(val input: CreateQuestionModel)(implicit val channelCtx: ChannelContext)
      extends Command[CreateQuestionModel, CreateQuestionResponseModel] {

    def execute(): Response = {
      userQuestionCount(make[UserQuestionsKey](authCtx.userId))
    }

    def userQuestionCount(key: UserQuestionsKey) =
      key.card flatMap {
        case count if count > maxUserQuestions => Errors.userCreatedTooManyQuestions
        case _ =>
          val id = RandomService().uuid
          createQuestion(id, make[QuestionKey](id))
      }

    def createQuestion(questionId: UUID, key: QuestionKey) =
      key.createDef(input.a, input.b, authCtx.userId) flatMap { result =>
        groupQuestionsAdd(questionIdComposites(questionId), make[GroupQuestionsKey](input.groupId))
      }

    def questionIdComposites(id: UUID) = List(QuestionIdComposite(id, SIDE_A)) ++ (input.b match {
      case Some(b) => List(QuestionIdComposite(id, SIDE_B))
      case None    => List()
    })

    def groupQuestionsAdd(questions: Seq[QuestionIdComposite], key: GroupQuestionsKey) =
      key.add(questions: _*) flatMap {
        case s if s == questions.size =>
          userQuestionsAdd(questions, make[UserQuestionsKey](authCtx.userId))
        case _ => Errors.groupQuestionsAddFailed
      }

    def userQuestionsAdd(questions: Seq[QuestionIdComposite], key: UserQuestionsKey) =
      key.add(questions: _*) flatMap {
        case s if s == questions.size =>
          allUserQuestionsAdd(questions, make[QuestionsKey](QuestionServiceImpl.userQuestionsKey))
        case _ => Errors.userQuestionsAddFailed
      }

    def allUserQuestionsAdd(questions: Seq[QuestionIdComposite], key: QuestionsKey) =
      key.add(questions: _*) flatMap {
        case s if s == questions.size =>
          CreateQuestionResponseModel(input.groupId, questions.head.questionId)
        case _ => Errors.allQuestionsAddFailed
      }

  }

}

object CreateQuestionCommandServiceImpl {

  object Errors extends CrudErrorImplicits[CreateQuestionResponseModel] {

    override protected val externalErrorText = "Create question failed."

    lazy val userCreatedTooManyQuestions: Response = "user has created too many questions"
    lazy val groupQuestionsAddFailed: Response = "failed to add question to group"
    lazy val userQuestionsAddFailed: Response = "failed to add question to user"
    lazy val allQuestionsAddFailed: Response = "failed to add question to all"

  }

}
