/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 10:50 PM
 */

package base.entity.group

import java.util.UUID

import base.common.service.{ Service, ServiceCompanion }
import base.entity.error.ApiError
import base.entity.event.model.EventModel
import base.entity.kv.Key.Pipeline

import scala.concurrent.Future

/**
 * CRUD, etc.
 * @author rconrad
 */
trait GroupEventsService extends Service {

  final def serviceManifest = manifest[GroupEventsService]

  def getEventCount(groupId: UUID)(implicit p: Pipeline): Future[Either[ApiError, Int]]

  def getEvents(groupId: UUID)(implicit p: Pipeline): Future[Either[ApiError, List[EventModel]]]

  def setEvent(event: EventModel,
               createIfNotExists: Boolean = false)(implicit p: Pipeline): Future[Either[ApiError, EventModel]]

}

object GroupEventsService extends ServiceCompanion[GroupEventsService]
