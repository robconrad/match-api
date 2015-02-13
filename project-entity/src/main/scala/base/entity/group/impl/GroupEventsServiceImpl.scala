/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/12/15 8:53 PM
 */

package base.entity.group.impl

import java.util.UUID

import base.common.service.ServiceImpl
import base.entity.event.model.EventModel
import base.entity.group.GroupEventsService
import base.entity.group.kv.GroupEventsKey
import base.entity.json.JsonFormats
import base.entity.kv.MakeKey
import base.entity.logging.AuthLoggable

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
    with MakeKey
    with AuthLoggable {

  private implicit val formats = JsonFormats.withModels

  def getEvents(groupId: UUID) = {
    val key = make[GroupEventsKey](groupId)
    key.lRange(0, count - 1).map { events =>
      Right(events)
    }
  }

  def setEvent(event: EventModel, createIfNotExists: Boolean) = {
    val key = make[GroupEventsKey](event.groupId)
    val fun: EventModel => Future[Long] = createIfNotExists match {
      case true => any => key.lPush(any)
      case false => key.lPushX
    }
    fun(event).map { result =>
      Right(event)
    }
  }

}
