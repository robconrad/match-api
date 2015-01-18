/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 9:24 AM
 */

package base.entity.question.impl

import base.entity.auth.context.AuthContext
import base.entity.command.Command
import base.entity.command.impl.CommandServiceImpl
import base.entity.event.model.EventModel
import base.entity.question.model.AnswerModel
import base.entity.question.{ AnswerCommandService, QuestionService }

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class AnswerCommandServiceImpl()
    extends CommandServiceImpl[AnswerModel, List[EventModel]]
    with AnswerCommandService {

  def innerExecute(input: AnswerModel)(implicit authCtx: AuthContext) = {
    new AnswerCommand(input).execute()
  }

  /**
   * - get and return questions
   */
  private[impl] class AnswerCommand(val input: AnswerModel)(implicit val authCtx: AuthContext)
      extends Command[AnswerModel, List[EventModel]] {

    def execute() = {
      answerSet()
    }

    def answerSet(): Response =
      QuestionService().answer(input).map {
        case Right(event) => event
        case Left(error)  => error
      }

  }

}
