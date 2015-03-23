/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/22/15 8:29 PM
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
    val eventId = randomMock.nextUuid()
    val event = EventModelFactory.message(eventId, None, group.id, socket)
    group.events ++= List(event)
    val messageModel = MessageModel(group.id, event.body)
    executor(messageModel, None)
    group.sockets.foreach { currentSocket =>
      val event = EventModelFactory.message(eventId, Option(group.model()(currentSocket)), group.id, socket)
      executor.assertResponse(event)(manifest[EventModel], currentSocket)
    }
    event
  }

}
