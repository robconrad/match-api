/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 5:08 PM
 */

package base.entity.group.model.impl

import java.util.UUID

import base.entity.group.model.{ InviteModel, GroupModel }
import base.entity.user.model.UserModel
import org.joda.time.DateTime

/**
 * API describable model
 * @author rconrad
 */
case class GroupModelImpl(id: UUID,
                          users: List[UserModel],
                          invites: List[InviteModel],
                          lastEventTime: Option[DateTime],
                          lastReadTime: Option[DateTime],
                          eventCount: Long) extends GroupModel

case class GroupModelBuilder(id: Option[UUID] = None,
                             users: Option[List[UserModel]] = None,
                             invites: Option[List[InviteModel]] = None,
                             lastEventTime: Option[Option[DateTime]] = None,
                             lastReadTime: Option[Option[DateTime]] = None,
                             eventCount: Option[Long] = None) {

  def build = GroupModelImpl(
    id = id.get,
    users = users.get,
    invites = invites.get,
    lastEventTime = lastEventTime.get,
    lastReadTime = lastReadTime.get,
    eventCount = eventCount.get)

}
