/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 10:25 PM
 */

package base.entity.group

import java.util.UUID

import base.common.service.{ Service, ServiceCompanion }
import base.entity.error.model.ApiError
import base.entity.event.model.EventModel

import scala.concurrent.Future

/**
 * CRUD, etc.
 * @author rconrad
 */
trait GroupEventsService extends Service {

  final val serviceManifest = manifest[GroupEventsService]

  def getEvents(groupId: UUID): Future[Either[ApiError, List[EventModel]]]

  def setEvent(event: EventModel,
               createIfNotExists: Boolean): Future[Either[ApiError, EventModel]]

}

object GroupEventsService extends ServiceCompanion[GroupEventsService]
