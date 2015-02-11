/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/10/15 12:37 PM
 */

package base.entity.question.impl

import base.entity.auth.context.ChannelContext
import base.entity.command.Command
import base.entity.command.impl.CommandServiceImpl
import base.entity.group.kv.GroupUsersKey
import base.entity.question.impl.QuestionsCommandServiceImpl.Errors
import base.entity.question.model.{ QuestionsModel, QuestionsResponseModel }
import base.entity.question.{ QuestionService, QuestionsCommandService }
import base.entity.service.CrudErrorImplicits

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class QuestionsCommandServiceImpl()
    extends CommandServiceImpl[QuestionsModel, QuestionsResponseModel]
    with QuestionsCommandService {

  override protected val responseManifest = Option(manifest[QuestionsResponseModel])

  def innerExecute(input: QuestionsModel)(implicit channelCtx: ChannelContext) = {
    new QuestionsCommand(input).execute()
  }

  /**
   * - check if member of group
   * - get and return questions
   */
  private[impl] class QuestionsCommand(val input: QuestionsModel)(implicit val channelCtx: ChannelContext)
      extends Command[QuestionsModel, QuestionsResponseModel] {

    def execute() = {
      groupUsersIsMember(make[GroupUsersKey](input.groupId))
    }

    def groupUsersIsMember(key: GroupUsersKey) =
      key.isMember(authCtx.userId) flatMap {
        case false => Errors.userNotGroupMember
        case true  => questionsGet()
      }

    def questionsGet(): Response =
      QuestionService().getQuestions(input.groupId, authCtx.userId).map {
        case Right(questions) => QuestionsResponseModel(input.groupId, questions)
        case Left(error)      => error
      }

  }

}

object QuestionsCommandServiceImpl {

  object Errors extends CrudErrorImplicits[QuestionsResponseModel] {

    override protected val externalErrorText = "Get questions failed."

    lazy val userNotGroupMember: Response = "user is not a member of the group"

  }

}
