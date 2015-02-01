/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 4:22 PM
 */

package base.entity.group.model.impl

import java.util.UUID

import base.entity.group.model.GroupModel
import base.entity.user.model.UserModel
import org.joda.time.DateTime

/**
 * API describable model
 * @author rconrad
 */
case class GroupModelImpl(id: UUID,
                          users: List[UserModel],
                          lastEventTime: Option[DateTime],
                          lastReadTime: Option[DateTime],
                          eventCount: Int) extends GroupModel

case class GroupModelBuilder(id: Option[UUID] = None,
                             users: Option[List[UserModel]] = None,
                             lastEventTime: Option[DateTime] = None,
                             lastReadTime: Option[DateTime] = None,
                             eventCount: Option[Int] = None) {

  def build = GroupModelImpl(
    id = id.get,
    users = users.get,
    lastEventTime = lastEventTime,
    lastReadTime = lastReadTime,
    eventCount = eventCount.get)

}
