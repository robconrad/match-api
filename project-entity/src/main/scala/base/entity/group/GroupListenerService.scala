/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/24/15 11:23 PM
 */

package base.entity.group

import java.util.UUID

import base.common.service.{ Service, ServiceCompanion }
import base.entity.auth.context.ChannelContext
import base.entity.command.model.CommandModel
import base.entity.event.model.EventModel

import scala.concurrent.Future

/**
 * CRUD, etc.
 * @author rconrad
 */
trait GroupListenerService extends Service {

  final val serviceManifest = manifest[GroupListenerService]

  def register(userId: UUID, groupIds: Set[UUID])(implicit channelCtx: ChannelContext): Future[Unit.type]

  def unregister(userId: UUID)(implicit channelCtx: ChannelContext): Future[Unit.type]

  def publish(event: CommandModel[EventModel])(implicit channelCtx: ChannelContext): Future[Unit.type]

}

object GroupListenerService extends ServiceCompanion[GroupListenerService]
