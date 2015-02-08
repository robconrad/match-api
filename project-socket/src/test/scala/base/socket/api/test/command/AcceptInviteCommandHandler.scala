/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 1:20 PM
 */

package base.socket.api.test.command

import java.util.UUID

import base.entity.event.model.EventModel
import base.entity.group.model.impl.GroupModelImpl
import base.entity.group.model.{AcceptInviteModel, AcceptInviteResponseModel}
import base.entity.user.model._
import base.socket.api.test.SocketConnection
import base.socket.api.test.utils.ListUtils._
import base.socket.api.test.utils.TestQuestions

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class AcceptInviteCommandHandler(implicit socket: SocketConnection) extends CommandHandler {

  def apply(users: List[UserModel], groupId: UUID, events: List[EventModel])
           (implicit executor: CommandExecutor, questions: TestQuestions) {
    val groupModel = GroupModelImpl(groupId, sortUsers(users), List(), None, None, 0)

    val acceptInviteModel = AcceptInviteModel(groupId)
    val inviteResponseModel = AcceptInviteResponseModel(groupModel, events, questions.models)
    executor(acceptInviteModel, Option(inviteResponseModel))
  }

}
