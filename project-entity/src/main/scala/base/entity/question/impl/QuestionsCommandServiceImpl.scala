/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 9:44 PM
 */

package base.entity.question.impl

import base.entity.auth.context.{ AuthContext, ChannelContext }
import base.entity.command.Command
import base.entity.command.impl.CommandServiceImpl
import base.entity.question.model.{ QuestionsModel, QuestionsResponseModel }
import base.entity.question.{ QuestionService, QuestionsCommandService }

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class QuestionsCommandServiceImpl()
    extends CommandServiceImpl[QuestionsModel, QuestionsResponseModel]
    with QuestionsCommandService {

  def innerExecute(input: QuestionsModel)(implicit channelCtx: ChannelContext) = {
    new QuestionsCommand(input).execute()
  }

  /**
   * - get and return questions
   */
  private[impl] class QuestionsCommand(val input: QuestionsModel)(implicit val channelCtx: ChannelContext)
      extends Command[QuestionsModel, QuestionsResponseModel] {

    def execute() = {
      questionsGet()
    }

    def questionsGet(): Response =
      QuestionService().getQuestions(input.groupId).map {
        case Right(questions) => QuestionsResponseModel(input.groupId, questions)
        case Left(error)      => error
      }

  }

}
