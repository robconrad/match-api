/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 4:16 PM
 */

package base.socket.api.test.command

import java.util.UUID

import base.common.random.mock.RandomServiceMock
import base.entity.message.model.MessageModel
import base.socket.api.test.SocketConnection
import base.socket.api.test.model.EventModelFactory

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class MessageCommandHandler(implicit socket: SocketConnection) extends CommandHandler {

  def apply(groupId: UUID)(implicit executor: CommandExecutor, randomMock: RandomServiceMock) = {
    val event = EventModelFactory.message(randomMock.nextUuid(), groupId, socket)
    val messageModel = MessageModel(groupId, event.body)
    executor(messageModel, None)
    executor.assertResponse(event)
    event
  }

}
