/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 6:57 PM
 */

package base.socket.api.test.command

import base.common.random.mock.RandomServiceMock
import base.entity.event.model.EventModel
import base.entity.message.model.MessageModel
import base.socket.api.test.model.EventModelFactory
import base.socket.api.test.{ SocketConnection, TestGroup }

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class MessageCommandHandler(implicit socket: SocketConnection) extends CommandHandler {

  def apply(group: TestGroup)(implicit executor: CommandExecutor, randomMock: RandomServiceMock) = {
    val event = EventModelFactory.message(randomMock.nextUuid(), group.id, socket)
    group.events ++= List(event)
    val messageModel = MessageModel(group.id, event.body)
    executor(messageModel, None)
    group.sockets.foreach { socket =>
      executor.assertResponse(event)(manifest[EventModel], socket)
    }
    event
  }

}
