/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 6:06 PM
 */

package base.socket.api.test.command

import base.common.random.mock.RandomServiceMock
import base.entity.auth.context.StandardUserAuthContext
import base.entity.auth.context.impl.ChannelContextImpl
import base.entity.event.model.EventModel
import base.entity.kv.Key.Pipeline
import base.entity.question.model.AnswerModel
import base.entity.question.{QuestionService, QuestionSides}
import base.entity.user.User
import base.socket.api._
import base.socket.api.test.model.EventModelFactory
import base.socket.api.test.util.TestQuestions
import base.socket.api.test.{SocketConnection, TestGroup}

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class AnswerCommandHandler(implicit socket: SocketConnection) extends CommandHandler {

  def apply(group: TestGroup, otherSocket: SocketConnection, questionIndex: Int)
           (implicit executor: CommandExecutor, questions: TestQuestions, randomMock: RandomServiceMock, tp: Pipeline) {
    val answerEventId = randomMock.nextUuid()
    val questionId = questions(questionIndex).id
    val answer = true
    val side = QuestionSides.SIDE_A
    val answerBody = questions.defs.find(_.id == questionId).get + " is a match"

    val otherUserAuthCtx = ChannelContextImpl(new StandardUserAuthContext(new User(otherSocket.userId)), None)
    val otherUserAnswerModel = AnswerModel(questionId, group.id, side, answer)
    QuestionService().answer(otherUserAnswerModel)(tp, otherUserAuthCtx).await()

    socket.answerQuestion(group.id, questionIndex)
    otherSocket.answerQuestion(group.id, questionIndex)

    val answerModel = AnswerModel(questionId, group.id, side, answer)
    val eventModel = EventModelFactory.`match`(answerEventId, group.id, answerBody)
    executor(answerModel, None)
    group.sockets.foreach { socket =>
      executor.assertResponse(eventModel)(manifest[EventModel], socket)
    }
  }

}
