/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/22/15 6:42 PM
 */

package base.entity.event.impl

import base.entity.auth.context.ChannelContext
import base.entity.command.Command
import base.entity.command.impl.CommandServiceImpl
import base.entity.event.AckEventsCommandService
import base.entity.event.impl.AckEventsCommandServiceImpl.Errors
import base.entity.event.model.impl.AckEventsResponseModelImpl
import base.entity.event.model.{ AckEventsModel, AckEventsResponseModel }
import base.entity.group.GroupService
import base.entity.group.kv.GroupUserKey
import base.entity.service.CrudErrorImplicits

/**
 * User processing (CRUD - i.e. external / customer-facing)
 * @author rconrad
 */
private[entity] class AckEventsCommandServiceImpl()
    extends CommandServiceImpl[AckEventsModel, AckEventsResponseModel]
    with AckEventsCommandService {

  override protected val responseManifest = Option(manifest[AckEventsResponseModel])

  def innerExecute(input: AckEventsModel)(implicit channelCtx: ChannelContext) = {
    assert(input.lastReadEventCount > 0L)
    new AckEventsCommand(input).execute()
  }

  /**
   * - mark events read for this user for the group specified
   */
  private[impl] class AckEventsCommand(val input: AckEventsModel)(implicit val channelCtx: ChannelContext)
      extends Command[AckEventsModel, AckEventsResponseModel] {

    def execute() = {
      val key = make[GroupUserKey]((input.groupId, authCtx.userId))
      key.setLastReadEventCount(input.lastReadEventCount) flatMap { result =>
        getGroup()
      }
    }

    def getGroup(): Response = GroupService().getGroup(authCtx.userId, input.groupId) map {
      case Right(Some(group)) => Right(AckEventsResponseModelImpl(group))
      case Right(group)       => Errors.groupGetFailed
      case Left(error)        => error
    }

  }

}

object AckEventsCommandServiceImpl {

  object Errors extends CrudErrorImplicits[AckEventsResponseModel] {

    override protected val externalErrorText = "There was a problem acknowledging events."

    lazy val groupGetFailed: EitherResponse = "failed to get group"

  }

}
