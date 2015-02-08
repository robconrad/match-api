/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 11:54 AM
 */

package base.socket.api.test.command

import java.util.UUID

import base.entity.event.model.EventModel
import base.entity.message.model.MessageModel
import base.socket.api.test.SocketConnection

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class MessageCommandHandler(implicit socket: SocketConnection) extends CommandHandler {

  def apply(groupId: UUID, event: EventModel)(implicit executor: CommandExecutor) {
    val messageModel = MessageModel(groupId, event.body)
    executor(messageModel, None)
    executor.assertResponse(event)
  }

}
