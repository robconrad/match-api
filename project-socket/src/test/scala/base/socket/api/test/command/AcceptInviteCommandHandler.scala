/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 11:50 AM
 */

package base.socket.api.test.command

import java.util.UUID

import base.entity.event.model.EventModel
import base.entity.group.model.impl.GroupModelImpl
import base.entity.group.model.{AcceptInviteModel, AcceptInviteResponseModel}
import base.entity.question.model.QuestionModel
import base.entity.user.model._
import base.socket.api.test.ListUtils._
import base.socket.api.test.SocketConnection

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class AcceptInviteCommandHandler(implicit socket: SocketConnection) extends CommandHandler {

  def apply(users: List[UserModel],
            groupId: UUID, events: List[EventModel],
            questionModels: List[QuestionModel])(implicit executor: CommandExecutor) {
    val groupModel = GroupModelImpl(groupId, sortUsers(users), List(), None, None, 0)

    val acceptInviteModel = AcceptInviteModel(groupId)
    val inviteResponseModel = AcceptInviteResponseModel(groupModel, events, sortQuestions(questionModels))
    executor(acceptInviteModel, Option(inviteResponseModel))
  }

}
