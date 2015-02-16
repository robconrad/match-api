/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 6:24 PM
 */

package base.entity.event.impl

import base.entity.auth.context.ChannelContext
import base.entity.command.Command
import base.entity.command.impl.CommandServiceImpl
import base.entity.event.AckEventsCommandService
import base.entity.event.model.AckEventsModel
import base.entity.group.kv.GroupUserKey
import base.entity.message.MessageCommandService
import base.entity.message.model.MessageModel

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class AckEventsCommandServiceImpl()
    extends CommandServiceImpl[AckEventsModel, Unit]
    with AckEventsCommandService {

  def innerExecute(input: AckEventsModel)(implicit channelCtx: ChannelContext) = {
    new AckEventsCommand(input).execute()
  }

  /**
   * - mark events read for this user for the group specified
   */
  private[impl] class AckEventsCommand(val input: AckEventsModel)(implicit val channelCtx: ChannelContext)
      extends Command[AckEventsModel, Unit] {

    def execute() = {
      val key = make[GroupUserKey]((input.groupId, authCtx.userId))
      key.setLastRead() map { result =>
        Right(Unit)
      }
    }

  }

}
