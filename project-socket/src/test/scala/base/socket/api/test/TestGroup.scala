/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 6:28 PM
 */

package base.socket.api.test

import java.util.UUID

import base.common.random.mock.RandomServiceMock
import base.entity.event.model.EventModel
import base.entity.group.model.impl.GroupModelImpl
import base.entity.group.model.{GroupModel, InviteModel}
import base.entity.user.model.UserModel
import base.socket.api._
import base.socket.api.test.model.{InviteModelFactory, EventModelFactory}
import base.socket.api.test.util.ListUtils._

/**
 * {{ Describe the high level purpose of TestGroup here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class TestGroup(private var _id: Option[UUID] = None,
                private var _sockets: List[SocketConnection] = List(),
                private var _users: List[UserModel] = List(),
                private var _invites: List[InviteModel] = List(),
                private var _events: List[EventModel] = List()) {

  def set(randomMock: RandomServiceMock, socket1: SocketConnection, socket2: SocketConnection) {
    id = randomMock.nextUuid()
    sockets = List(socket1)
    users = List(socket1.userModel)
    invites = List(InviteModelFactory(socket2.phoneString))
    events = List(EventModelFactory.welcome(randomMock.nextUuid(1), randomMock.nextUuid()))
  }

  def model: GroupModel = GroupModelImpl(id, users, invites, None, None, 0)

  def id_=(id: UUID) { _id = Option(id) }
  def id = _id.get

  def sockets_=(sockets: List[SocketConnection]) { _sockets = sockets }
  def sockets = _sockets

  def users_=(users: List[UserModel]) { _users = sortUsers(users) }
  def users = _users

  def invites_=(invites: List[InviteModel]) { _invites = invites }
  def invites = _invites

  def events_=(events: List[EventModel]) { _events = events }
  def events = _events

}
