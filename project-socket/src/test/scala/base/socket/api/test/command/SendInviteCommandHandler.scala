/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 6:59 PM
 */

package base.socket.api.test.command

import base.common.random.mock.RandomServiceMock
import base.entity.group.model.{ SendInviteModel, SendInviteResponseModel }
import base.socket.api.test.model.InviteModelFactory
import base.socket.api._
import base.socket.api.test.util.TestQuestions
import base.socket.api.test.{ SocketConnection, TestGroup }

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class SendInviteCommandHandler(implicit socket: SocketConnection) extends CommandHandler {

  def apply(group: TestGroup,
            invitedSocket: SocketConnection)(implicit executor: CommandExecutor,
                                             questions: TestQuestions, randomMock: RandomServiceMock) = {

    group.set(randomMock, socket, invitedSocket)

    socket.groups ++= List(group)
    invitedSocket.pendingGroups ++= List(group)

    val inviteModel = SendInviteModel(invitedSocket.phoneString, InviteModelFactory.label)
    val inviteResponseModel = SendInviteResponseModel(group.model, group.events, questions.models)
    executor(inviteModel, Option(inviteResponseModel))
    group
  }

}
