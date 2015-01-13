/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/12/15 8:45 PM
 */

package base.entity.event

import java.util.UUID

import base.common.service.{ Service, ServiceCompanion }
import base.entity.error.ApiError
import base.entity.event.model.EventModel
import base.entity.kv.Key.Pipeline

import scala.concurrent.Future

/**
 * Pair CRUD, etc.
 * @author rconrad
 */
trait EventService extends Service {

  final def serviceManifest = manifest[EventService]

  def getEvents(pairId: UUID)(implicit p: Pipeline): Future[Either[ApiError, List[EventModel]]]

}

object EventService extends ServiceCompanion[EventService] {

}
