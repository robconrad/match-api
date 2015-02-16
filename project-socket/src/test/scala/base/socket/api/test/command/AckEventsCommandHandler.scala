/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:13 PM
 */

package base.socket.api.test.command

import base.common.time.mock.TimeServiceConstantMock
import base.entity.event.model.AckEventsModel
import base.entity.event.model.impl.AckEventsModelImpl
import base.socket.api._
import base.socket.api.test.{ SocketConnection, TestGroup }

/**
 * {{ Describe the high level purpose of LoginCommandHandler here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class AckEventsCommandHandler(implicit socket: SocketConnection) extends CommandHandler {

  def apply(group: TestGroup)(implicit executor: CommandExecutor) = {

    group.reads += socket.userId -> TimeServiceConstantMock.now

    val ackEvents: AckEventsModel = AckEventsModelImpl(group.id)
    executor(ackEvents, None)
  }

}
