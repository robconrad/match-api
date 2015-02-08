/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 12:05 PM
 */

package base.socket.api.test.command

import java.util.UUID

import base.entity.event.model.EventModel
import base.entity.group.model.impl.{GroupModelImpl, InviteModelImpl}
import base.entity.group.model.{SendInviteModel, SendInviteResponseModel}
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
class SendInviteCommandHandler(implicit socket: SocketConnection) extends CommandHandler {

  def apply(phone: String, users: List[UserModel], groupId: UUID, events: List[EventModel],
            questionModels: List[QuestionModel])(implicit executor: CommandExecutor) {
    val label = "bob"
    val invite = InviteModelImpl(phone, None, Option(label))
    val groupModel = GroupModelImpl(groupId, sortUsers(users), List(invite), None, None, 0)

    val inviteModel = SendInviteModel(phone, label)
    val inviteResponseModel = SendInviteResponseModel(groupModel, events, sortQuestions(questionModels))
    executor(inviteModel, Option(inviteResponseModel))
  }

}
