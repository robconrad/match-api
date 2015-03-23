/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/22/15 8:31 PM
 */

package base.socket.api.test.command

import base.common.random.mock.RandomServiceMock
import base.entity.group.model.{ AcceptInviteModel, AcceptInviteResponseModel }
import base.socket.api._
import base.socket.api.test.model.EventModelFactory
import base.socket.api.test.util.TestQuestions
import base.socket.api.test.{ SocketConnection, TestGroup }

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class AcceptInviteCommandHandler(implicit socket: SocketConnection) extends CommandHandler {

  def apply(group: TestGroup)(implicit executor: CommandExecutor,
                              questions: TestQuestions, randomMock: RandomServiceMock) {

    socket.groups ++= List(group)
    socket.pendingGroups = socket.pendingGroups.filter(_ != group)

    group.sockets ++= List(socket)
    group.users ++= List(socket.userModel)
    group.invites = List()
    group.events ++= List(EventModelFactory.join(randomMock.nextUuid(), None, group.id, socket))

    val acceptInviteModel = AcceptInviteModel(group.id)
    val inviteResponseModel = AcceptInviteResponseModel(
      group.model(hydrate = true),
      group.events.reverse,
      questions.models(group.id))
    executor(acceptInviteModel, Option(inviteResponseModel))
  }

}
