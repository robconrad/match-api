/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 10:56 PM
 */

package base.entity.group.impl

import java.util.UUID

import base.common.service.ServiceImpl
import base.entity.event.model.EventModel
import base.entity.group.GroupEventsService
import base.entity.kv.Key._
import base.entity.logging.AuthLoggable

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of GroupEventsServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupEventsServiceImpl
    extends ServiceImpl
    with GroupEventsService
    with AuthLoggable {

  def getEventCount(groupId: UUID)(implicit p: Pipeline) = {
    Future.failed(new RuntimeException())
  }

  def getEvents(groupId: UUID)(implicit p: Pipeline) = {
    Future.failed(new RuntimeException())
  }

  def setEvent(event: EventModel, createIfNotExists: Boolean)(implicit p: Pipeline) = {
    Future.failed(new RuntimeException())
  }

}
