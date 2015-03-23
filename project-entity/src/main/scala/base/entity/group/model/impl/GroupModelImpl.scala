/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/22/15 5:05 PM
 */

package base.entity.group.model.impl

import java.util.UUID

import base.entity.group.model.{ GroupModel, InviteModel }
import base.entity.user.model.UserModel
import org.joda.time.DateTime

/**
 * API describable model
 * @author rconrad
 */
case class GroupModelImpl(id: UUID,
                          users: Option[List[UserModel]],
                          invites: Option[List[InviteModel]],
                          lastReadEventCount: Option[Long],
                          eventCount: Long) extends GroupModel

case class GroupModelBuilder(id: Option[UUID] = None,
                             users: Option[Option[List[UserModel]]] = None,
                             invites: Option[Option[List[InviteModel]]] = None,
                             lastReadEventCount: Option[Option[Long]] = None,
                             eventCount: Option[Long] = None) {

  def build = GroupModelImpl(
    id = id.get,
    users = users.get,
    invites = invites.get,
    lastReadEventCount = lastReadEventCount.get,
    eventCount = eventCount.get)

}
