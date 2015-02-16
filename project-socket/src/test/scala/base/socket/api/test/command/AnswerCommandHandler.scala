/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:13 PM
 */

package base.socket.api.test.command

import base.common.logging.Loggable
import base.common.random.mock.RandomServiceMock
import base.entity.auth.context.StandardUserAuthContext
import base.entity.auth.context.impl.ChannelContextImpl
import base.entity.event.model.EventModel
import base.entity.question.QuestionService
import base.entity.question.QuestionSides._
import base.entity.question.model.AnswerModel
import base.entity.user.User
import base.socket.api._
import base.socket.api.test.model.EventModelFactory
import base.socket.api.test.util.TestQuestions
import base.socket.api.test.{ SocketConnection, TestGroup }

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class AnswerCommandHandler(implicit socket: SocketConnection) extends CommandHandler with Loggable {

  def apply(group: TestGroup,
            otherSocket: SocketConnection)(implicit executor: CommandExecutor,
                                           questions: TestQuestions, randomMock: RandomServiceMock) {
    val answerEventId = randomMock.nextUuid()

    val (preIndex, side) = socket.questionsAnswered(group.id).sorted.lastOption.getOrElse((-1, SIDE_A))
    val index = preIndex + 1
    assert(!otherSocket.questionsAnswered(group.id).contains((index, side)))

    val question = questions(group.id, index)
    val answer = true
    val answerBody = questions.defs(group.id).find(_.id == question.id).get + " is a match"

    val otherSide = question.b.isDefined match {
      case true if side == SIDE_A => SIDE_B
      case true if side == SIDE_B => SIDE_A
      case false                  => side
    }

    val otherUserAuthCtx =
      ChannelContextImpl(new StandardUserAuthContext(new User(otherSocket.userId), Set(group.id)), None)
    val otherUserAnswerModel = AnswerModel(question.id, group.id, otherSide, answer)
    QuestionService().answer(otherUserAnswerModel)(otherUserAuthCtx).await()

    socket.answerQuestion(group.id, index, side)
    otherSocket.answerQuestion(group.id, index, otherSide)

    val answerModel = AnswerModel(question.id, group.id, side, answer)
    val eventModel = EventModelFactory.`match`(answerEventId, group.id, answerBody)
    executor(answerModel, None)
    group.sockets.foreach { socket =>
      debug("expect response for %s", socket.hashCode())
      executor.assertResponse(eventModel)(manifest[EventModel], socket)
    }
  }

}
