/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 9:41 AM
 */

package base.entity.question.impl

import base.entity.auth.context.ChannelContext
import base.entity.command.Command
import base.entity.command.impl.CommandServiceImpl
import base.entity.command.model.CommandModel
import base.entity.event.model.EventModel
import base.entity.group.GroupListenerService
import base.entity.question.model.AnswerModel
import base.entity.question.{ AnswerCommandService, QuestionService }

import scala.concurrent.Future

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class AnswerCommandServiceImpl()
    extends CommandServiceImpl[AnswerModel, Unit]
    with AnswerCommandService {

  def innerExecute(input: AnswerModel)(implicit channelCtx: ChannelContext) = {
    new AnswerCommand(input).execute()
  }

  /**
   * - get and return questions
   */
  private[impl] class AnswerCommand(val input: AnswerModel)(implicit val channelCtx: ChannelContext)
      extends Command[AnswerModel, Unit] {

    def execute() = {
      answerSet()
    }

    def answerSet(): Response =
      QuestionService().answer(input).flatMap {
        case Right(events) => publishMatches(events)
        case Left(error)   => error
      }

    // todo test this
    def publishMatches(events: List[EventModel]): Response = {
      val futures = events.map { event =>
        GroupListenerService().publish(CommandModel(event))
      }
      Future.sequence(futures).map { x =>
        Right(Unit)
      }
    }

  }

}
