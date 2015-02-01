/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 8:59 AM
 */

package base.entity.group.impl

import java.util.UUID

import base.common.service.ServiceImpl
import base.entity.error.ApiErrorService
import base.entity.event.model.EventModel
import base.entity.group.GroupEventsService
import base.entity.group.impl.GroupEventsServiceImpl.Errors
import base.entity.group.kv.GroupEventsKeyService
import base.entity.json.JsonFormats
import base.entity.kv.Key._
import base.entity.logging.AuthLoggable
import base.entity.service.CrudErrorImplicits
import spray.http.StatusCodes._

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of GroupEventsServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupEventsServiceImpl(count: Int)
    extends ServiceImpl
    with GroupEventsService
    with AuthLoggable {

  private implicit val formats = JsonFormats.withModels

  def getEvents(groupId: UUID)(implicit p: Pipeline) = {
    val key = GroupEventsKeyService().make(groupId)
    key.range(0, count - 1).map { events =>
      Right(events)
    }
  }

  def setEvent(event: EventModel, createIfNotExists: Boolean)(implicit p: Pipeline) = {
    val key = GroupEventsKeyService().make(event.groupId)
    val fun: EventModel => Future[Boolean] = createIfNotExists match {
      case true => any => key.prepend(any)
      case false => key.prependIfExists
    }
    fun(event).map {
      case true  => Right(event)
      case false => Errors.setEventFailed
    }
  }

}

object GroupEventsServiceImpl {

  object Errors extends CrudErrorImplicits[EventModel] {

    lazy val setEventFailed =
      Left(ApiErrorService().statusCodeSeed(externalErrorText, InternalServerError, "failed to set event"))

  }

}
