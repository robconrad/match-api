/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/22/15 5:43 PM
 */

package base.entity.event.model.impl

import java.util.UUID

import base.common.time.TimeService
import base.entity.event.EventTypes._
import base.entity.event.model.EventModel
import base.entity.group.model.GroupModel
import org.joda.time.DateTime

/**
 * {{ Describe the high level purpose of EventModelImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
case class EventModelImpl(
  id: UUID,
  group: Option[GroupModel] = None,
  groupId: UUID,
  userId: Option[UUID] = None,
  `type`: EventType,
  body: String,
  time: DateTime = TimeService().now) extends EventModel
