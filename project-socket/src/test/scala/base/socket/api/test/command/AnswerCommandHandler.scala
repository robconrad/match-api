/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 2:27 PM
 */

package base.socket.api.test.command

import java.util.UUID

import base.entity.auth.context.StandardUserAuthContext
import base.entity.auth.context.impl.ChannelContextImpl
import base.entity.kv.Key.Pipeline
import base.entity.question.model.AnswerModel
import base.entity.question.{QuestionService, QuestionSides}
import base.entity.user.User
import base.socket.api.test.SocketConnection
import base.socket.api.test.model.EventModelFactory
import base.socket.api.test.util.TestQuestions

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class AnswerCommandHandler(implicit socket: SocketConnection) extends CommandHandler {

  def apply(answerEventId: UUID, groupId: UUID, otherUserId: UUID, questionIndex: Int)
           (implicit executor: CommandExecutor, questions: TestQuestions, tp: Pipeline) = {
    val questionId = questions(0).id
    val answer = true
    val side = QuestionSides.SIDE_A
    val answerBody = questions.defs.find(_.id == questionId).get + " is a match"

    val inviteUserAuthCtx = ChannelContextImpl(new StandardUserAuthContext(new User(otherUserId)), None)
    val inviteUserAnswerModel = AnswerModel(questionId, groupId, side, answer)
    QuestionService().answer(inviteUserAnswerModel)(tp, inviteUserAuthCtx).await()

    val answerModel = AnswerModel(questionId, groupId, side, answer)
    val eventModel = EventModelFactory.`match`(answerEventId, groupId, answerBody)
    executor(answerModel, None)
    executor.assertResponse(eventModel)
    eventModel
  }

}
