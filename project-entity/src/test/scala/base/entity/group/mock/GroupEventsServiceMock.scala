/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 6:25 PM
 */

package base.entity.group.mock

import java.util.UUID

import base.entity.error.ApiError
import base.entity.event.model.EventModel
import base.entity.group.GroupEventsService
import base.entity.kv.Key._

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of GroupServiceMock here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
// scalastyle:off line.size.limit
class GroupEventsServiceMock(getEventsResult: Future[Either[ApiError, List[EventModel]]] = Future.successful(Right(List())),
                             setEventResult: Option[Future[Either[ApiError, EventModel]]] = None)
    extends GroupEventsService {

  def getEvents(groupId: UUID)(implicit p: Pipeline) = getEventsResult

  def setEvent(event: EventModel, createIfNotExists: Boolean)(implicit p: Pipeline) = setEventResult match {
    case Some(result) => result
    case None         => Future.successful(Right(event))
  }

}
