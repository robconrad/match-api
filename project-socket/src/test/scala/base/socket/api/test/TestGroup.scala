/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 2:41 PM
 */

package base.socket.api.test

import java.util.UUID

import base.entity.group.model.{InviteModel, GroupModel}
import base.entity.group.model.impl.GroupModelImpl
import base.entity.user.model.UserModel
import base.socket.api.test.model.EventModelFactory
import base.socket.api.test.util.ListUtils._

/**
 * {{ Describe the high level purpose of TestGroup here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class TestGroup(val id: UUID,
                private var _users: List[UserModel] = List(),
                private var _invites: List[InviteModel] = List()) {

  def welcome(eventId: UUID) = EventModelFactory.welcome(eventId, id)

  def model: GroupModel = GroupModelImpl(id, users, invites, None, None, 0)

  def users_=(users: List[UserModel]) { _users = sortUsers(users) }
  def users = _users

  def invites_=(invites: List[InviteModel]) { _invites = invites }
  def invites = _invites

}
