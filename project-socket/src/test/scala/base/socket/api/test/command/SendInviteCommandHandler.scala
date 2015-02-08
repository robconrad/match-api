/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 2:16 PM
 */

package base.socket.api.test.command

import java.util.UUID

import base.entity.event.model.EventModel
import base.entity.group.model.impl.{GroupModelImpl, InviteModelImpl}
import base.entity.group.model.{SendInviteModel, SendInviteResponseModel}
import base.entity.user.model._
import base.socket.api.test.SocketConnection
import base.socket.api.test.util.ListUtils._
import base.socket.api.test.util.TestQuestions

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class SendInviteCommandHandler(implicit socket: SocketConnection) extends CommandHandler {

  def apply(phone: String, users: List[UserModel], groupId: UUID, events: List[EventModel])
           (implicit executor: CommandExecutor, questions: TestQuestions) {
    val label = "bob"
    val invite = InviteModelImpl(phone, None, Option(label))
    val groupModel = GroupModelImpl(groupId, sortUsers(users), List(invite), None, None, 0)

    val inviteModel = SendInviteModel(phone, label)
    val inviteResponseModel = SendInviteResponseModel(groupModel, events, questions.models)
    executor(inviteModel, Option(inviteResponseModel))
  }

}
