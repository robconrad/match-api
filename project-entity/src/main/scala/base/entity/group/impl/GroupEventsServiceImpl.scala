/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 8:08 PM
 */

package base.entity.group.impl

import java.util.UUID

import base.common.service.ServiceImpl
import base.entity.error.ApiError
import base.entity.event.model.EventModel
import base.entity.group.GroupEventsService
import base.entity.group.impl.GroupEventsServiceImpl.Errors
import base.entity.group.kv.GroupEventsKeyService
import base.entity.json.JsonFormats
import base.entity.kv.Key._
import base.entity.kv.KeyId
import base.entity.logging.AuthLoggable
import base.entity.service.CrudErrorImplicits
import org.json4s.jackson.JsonMethods
import org.json4s.native.Serialization
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

  private implicit val formats = JsonFormats.withEnumsAndFields

  def getEvents(groupId: UUID)(implicit p: Pipeline) = {
    val key = GroupEventsKeyService().make(KeyId(groupId))
    key.range(0, count - 1).map { events =>
      val res = events.map { event =>
        JsonMethods.parse(event).extract[EventModel]
      }
      Right(res)
    }
  }

  def setEvent(event: EventModel, createIfNotExists: Boolean)(implicit p: Pipeline) = {
    val key = GroupEventsKeyService().make(KeyId(event.groupId))
    val fun: Any => Future[Boolean] = createIfNotExists match {
      case true => any => key.prepend(any)
      case false => key.prependIfExists
    }
    fun(Serialization.write(event)).map {
      case true  => Right(event)
      case false => Errors.setEventFailed
    }
  }

}

object GroupEventsServiceImpl {

  object Errors extends CrudErrorImplicits[EventModel] {

    lazy val setEventFailed = Left(ApiError(externalErrorText, InternalServerError, "failed to set event"))

  }

}
