/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 5:23 PM
 */

package base.entity.group

import java.util.UUID

import base.common.service.{ Service, ServiceCompanion }
import base.entity.auth.context.ChannelContext
import base.entity.error.model.ApiError
import base.entity.event.model.EventModel

import scala.concurrent.Future

/**
 * CRUD, etc.
 * @author rconrad
 */
trait GroupEventsService extends Service {

  final val serviceManifest = manifest[GroupEventsService]

  def getEvents(groupId: UUID,
                setLastReadTime: Boolean = true)
               (implicit channelCtx: ChannelContext): Future[Either[ApiError, List[EventModel]]]

  def setEvent(event: EventModel,
               createIfNotExists: Boolean): Future[Either[ApiError, EventModel]]

}

object GroupEventsService extends ServiceCompanion[GroupEventsService]
