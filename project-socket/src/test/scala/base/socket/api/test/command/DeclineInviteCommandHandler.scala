/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 6:57 PM
 */

package base.socket.api.test.command

import base.entity.group.model.{ DeclineInviteModel, DeclineInviteResponseModel }
import base.socket.api._
import base.socket.api.test.{ SocketConnection, TestGroup }

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class DeclineInviteCommandHandler(implicit socket: SocketConnection) extends CommandHandler {

  def apply(group: TestGroup)(implicit executor: CommandExecutor) {

    socket.pendingGroups = socket.pendingGroups.filter(_ != group)

    val declineInviteModel = DeclineInviteModel(group.id)
    val responseModel = DeclineInviteResponseModel(group.id)
    executor(declineInviteModel, Option(responseModel))
  }

}
