/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 10:50 PM
 */

package base.entity.message.impl

import base.common.random.RandomService
import base.entity.auth.context.AuthContext
import base.entity.command.Command
import base.entity.command.impl.CommandServiceImpl
import base.entity.event.EventTypes
import base.entity.event.model.EventModel
import base.entity.group.GroupEventsService
import base.entity.message.MessageCommandService
import base.entity.message.model.MessageModel

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class MessageCommandServiceImpl()
    extends CommandServiceImpl[MessageModel, EventModel]
    with MessageCommandService {

  def innerExecute(input: MessageModel)(implicit authCtx: AuthContext) = {
    new MessageCommand(input).execute()
  }

  /**
   * - add event to group events if group events exists
   * - return event
   */
  private[impl] class MessageCommand(val input: MessageModel)(implicit val authCtx: AuthContext)
      extends Command[MessageModel, EventModel] {

    def execute() = {
      groupEventSet()
    }

    def groupEventSet(): Response = {
      val event = EventModel(
        id = RandomService().uuid,
        groupId = input.groupId,
        userId = Option(authCtx.userId),
        `type` = EventTypes.MESSAGE,
        body = input.body)
      GroupEventsService().setEvent(event).map {
        case Right(event) => event
        case Left(error)  => error
      }
    }

  }

}
