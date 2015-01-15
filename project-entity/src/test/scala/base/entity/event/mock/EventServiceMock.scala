/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 1:08 PM
 */

package base.entity.event.mock

import java.util.UUID

import base.entity.error.ApiError
import base.entity.event.EventService
import base.entity.event.model.EventModel
import base.entity.kv.Key.Pipeline

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of EventServiceMock here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class EventServiceMock(getEventsResult: Future[Either[ApiError, List[EventModel]]] = Future.successful(Right(List())))
    extends EventService {

  def getEvents(groupId: UUID)(implicit p: Pipeline) = getEventsResult

}
