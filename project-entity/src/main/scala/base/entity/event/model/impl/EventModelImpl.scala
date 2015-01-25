/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 11:16 AM
 */

package base.entity.event.model.impl

import java.util.UUID

import base.common.time.TimeService
import base.entity.event.EventTypes._
import base.entity.event.model.EventModel
import org.joda.time.DateTime

/**
 * {{ Describe the high level purpose of EventModelImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
case class EventModelImpl(
  id: UUID,
  groupId: UUID,
  userId: Option[UUID] = None,
  `type`: EventType,
  body: String,
  time: DateTime = TimeService().now) extends EventModel
