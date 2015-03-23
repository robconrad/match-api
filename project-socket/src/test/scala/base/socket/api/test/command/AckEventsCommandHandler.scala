/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/22/15 8:25 PM
 */

package base.socket.api.test.command

import base.common.logging.Loggable
import base.entity.event.model.{ AckEventsResponseModel, AckEventsModel }
import base.entity.event.model.impl.{ AckEventsResponseModelImpl, AckEventsModelImpl }
import base.socket.api._
import base.socket.api.test.{ SocketConnection, TestGroup }

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class AckEventsCommandHandler(implicit socket: SocketConnection) extends CommandHandler with Loggable {

  def apply(group: TestGroup)(implicit executor: CommandExecutor) = {

    group.reads += socket.userId -> group.events.size

    val ackEvents: AckEventsModel = AckEventsModelImpl(group.id, group.events.size)
    val ackEventsResponse: AckEventsResponseModel = AckEventsResponseModelImpl(group.model())
    executor(ackEvents, Option(ackEventsResponse))
  }

}
