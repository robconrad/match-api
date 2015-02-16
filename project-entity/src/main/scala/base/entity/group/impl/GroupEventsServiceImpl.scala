/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 5:49 PM
 */

package base.entity.group.impl

import java.util.UUID

import base.common.service.ServiceImpl
import base.entity.auth.context.ChannelContext
import base.entity.event.model.EventModel
import base.entity.group.GroupEventsService
import base.entity.group.kv.{GroupUserKey, GroupKey, GroupEventsKey}
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
class GroupEventsServiceImpl(count: Int, store: Int, delta: Int)
    extends ServiceImpl
    with GroupEventsService
    with MakeKey
    with AuthLoggable {

  private implicit val formats = JsonFormats.withModels

  private val storeDelta = store + delta

  def getEvents(groupId: UUID)(implicit channelCtx: ChannelContext) = {
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
    fun(event) flatMap {
      case 0 => Future.successful(Right(event))
      case n if n > storeDelta =>
        groupSet(event.groupId) flatMap { result =>
          key.lTrim(0, store - 1) map { result =>
            Right(event)
          }
        }
      case n =>
        groupSet(event.groupId) map { result =>
          Right(event)
        }
    }
  }

  private def groupSet(groupId: UUID) =
    make[GroupKey](groupId).setLastEventAndIncrCount()

}
