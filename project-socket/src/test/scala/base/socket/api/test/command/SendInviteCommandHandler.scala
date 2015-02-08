/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 3:36 PM
 */

package base.socket.api.test.command

import base.entity.event.model.EventModel
import base.entity.group.model.{SendInviteModel, SendInviteResponseModel}
import base.socket.api.test.model.InviteModelFactory
import base.socket.api.test.util.TestQuestions
import base.socket.api.test.{SocketConnection, TestGroup}

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class SendInviteCommandHandler(implicit socket: SocketConnection) extends CommandHandler {

  def apply(phone: String, group: TestGroup, events: List[EventModel])
           (implicit executor: CommandExecutor, questions: TestQuestions) {
    val inviteModel = SendInviteModel(phone, InviteModelFactory.label)
    val inviteResponseModel = SendInviteResponseModel(group.model, events, questions.models)
    executor(inviteModel, Option(inviteResponseModel))
  }

}
