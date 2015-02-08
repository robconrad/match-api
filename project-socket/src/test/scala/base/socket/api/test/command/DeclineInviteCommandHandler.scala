/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 11:52 AM
 */

package base.socket.api.test.command

import java.util.UUID

import base.entity.group.model.{DeclineInviteModel, DeclineInviteResponseModel}
import base.socket.api.test.SocketConnection

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class DeclineInviteCommandHandler(implicit socket: SocketConnection) extends CommandHandler {

  def apply(groupId: UUID)(implicit executor: CommandExecutor) {
    val declineInviteModel = DeclineInviteModel(groupId)
    val responseModel = DeclineInviteResponseModel(groupId)
    executor(declineInviteModel, Option(responseModel))
  }

}
