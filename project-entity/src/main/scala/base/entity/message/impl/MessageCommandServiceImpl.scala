/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 10:08 AM
 */

package base.entity.message.impl

import base.common.random.RandomService
import base.entity.auth.context.ChannelContext
import base.entity.command.Command
import base.entity.command.impl.CommandServiceImpl
import base.entity.command.model.CommandModel
import base.entity.event.EventTypes
import base.entity.event.model.EventModel
import base.entity.event.model.impl.EventModelImpl
import base.entity.group.{ GroupEventsService, GroupListenerService }
import base.entity.message.MessageCommandService
import base.entity.message.model.MessageModel

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class MessageCommandServiceImpl()
    extends CommandServiceImpl[MessageModel, Unit]
    with MessageCommandService {

  def innerExecute(input: MessageModel)(implicit channelCtx: ChannelContext) = {
    new MessageCommand(input).execute()
  }

  /**
   * - add event to group events if group events exists
   * - return event
   */
  private[impl] class MessageCommand(val input: MessageModel)(implicit val channelCtx: ChannelContext)
      extends Command[MessageModel, Unit] {

    def execute() = {
      groupEventSet()
    }

    def groupEventSet(): Response = {
      val event = EventModelImpl(
        id = RandomService().uuid,
        groupId = input.groupId,
        userId = Option(authCtx.userId),
        `type` = EventTypes.MESSAGE,
        body = input.text)
      GroupEventsService().setEvent(event, createIfNotExists = false).flatMap {
        case Right(event) => publishEvent(event)
        case Left(error)  => error
      }
    }

    def publishEvent(event: EventModel): Response = {
      GroupListenerService().publish(CommandModel(event)).map { x =>
        Right(Unit)
      }
    }

  }

}
