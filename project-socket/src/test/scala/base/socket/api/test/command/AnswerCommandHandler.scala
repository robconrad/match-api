/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 12:02 PM
 */

package base.socket.api.test.command

import java.util.UUID

import base.common.random.mock.RandomServiceMock
import base.entity.auth.context.StandardUserAuthContext
import base.entity.auth.context.impl.ChannelContextImpl
import base.entity.event.EventTypes
import base.entity.event.model.EventModel
import base.entity.event.model.impl.EventModelImpl
import base.entity.kv.Key.Pipeline
import base.entity.question.model.AnswerModel
import base.entity.question.{QuestionDef, QuestionService, QuestionSides}
import base.entity.user.User
import base.socket.api.test.SocketConnection

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class AnswerCommandHandler(implicit socket: SocketConnection) extends CommandHandler {

  def apply(groupId: UUID, otherUserId: UUID, questionId: UUID)
           (implicit executor: CommandExecutor, randomMock: RandomServiceMock, questionDefs: List[QuestionDef], tp: Pipeline) = {
    val answer = true
    val side = QuestionSides.SIDE_A
    val answerEventId = randomMock.nextUuid()
    val answerBody = questionDefs.find(_.id == questionId).get + " is a match"

    val inviteUserAuthCtx = ChannelContextImpl(new StandardUserAuthContext(new User(otherUserId)), None)
    val inviteUserAnswerModel = AnswerModel(questionId, groupId, side, answer)
    QuestionService().answer(inviteUserAnswerModel)(tp, inviteUserAuthCtx).await()

    val answerModel = AnswerModel(questionId, groupId, side, answer)
    val eventModel: EventModel = EventModelImpl(answerEventId, groupId, None, EventTypes.MATCH, answerBody)
    executor(answerModel, None)
    executor.assertResponse(eventModel)
    eventModel
  }

}
