/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 5:48 PM
 */

package base.socket.api.test.command

import base.common.random.mock.RandomServiceMock
import base.entity.group.model.{SendInviteModel, SendInviteResponseModel}
import base.socket.api.test.model.InviteModelFactory
import base.socket.api._
import base.socket.api.test.util.TestQuestions
import base.socket.api.test.{SocketConnection, TestGroup}

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class SendInviteCommandHandler(implicit socket: SocketConnection) extends CommandHandler {

  def apply(invitedSocket: SocketConnection)
           (implicit executor: CommandExecutor, questions: TestQuestions, randomMock: RandomServiceMock) = {
    val group = new TestGroup(randomMock, socket, invitedSocket)
    val inviteModel = SendInviteModel(invitedSocket.phoneString, InviteModelFactory.label)
    val inviteResponseModel = SendInviteResponseModel(group.model, group.events, questions.models)
    executor(inviteModel, Option(inviteResponseModel))
    group
  }

}
