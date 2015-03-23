/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/22/15 7:45 PM
 */

package base.socket.api.test

import java.util.UUID

import base.common.random.mock.RandomServiceMock
import base.common.time.mock.TimeServiceConstantMock
import base.entity.event.model.EventModel
import base.entity.group.model.impl.GroupModelImpl
import base.entity.group.model.{ GroupModel, InviteModel }
import base.entity.user.model.UserModel
import base.socket.api._
import base.socket.api.test.model.{ EventModelFactory, InviteModelFactory }
import base.socket.api.test.util.ListUtils._
import org.joda.time.DateTime

/**
 * {{ Describe the high level purpose of TestGroup here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class TestGroup(private var _id: Option[UUID] = None,
                private var _sockets: Set[SocketConnection] = Set(),
                private var _users: List[UserModel] = List(),
                private var _invites: List[InviteModel] = List(),
                private var _events: List[EventModel] = List(),
                private var _reads: Map[UUID, Long] = Map()) {

  def set(randomMock: RandomServiceMock, socket1: SocketConnection, socket2: SocketConnection) {
    id = randomMock.nextUuid()
    sockets = Set(socket1)
    users = List(socket1.userModel)
    invites = socket2.phoneOpt match {
      case Some(userId) => List(socket2.inviteModel)
      case None         => List(InviteModelFactory(socket2.phoneString))
    }
    events = List(EventModelFactory.welcome(randomMock.nextUuid(1), None, randomMock.nextUuid()))
  }

  def model(hydrate: Boolean = false)(implicit s: SocketConnection): GroupModel = {
    GroupModelImpl(
      id,
      if (hydrate) Option(users) else None,
      if (hydrate) Option(invites) else None,
      reads.get(s.userId),
      events.size)
  }

  def id_=(id: UUID) { _id = Option(id) }
  def id = _id.get

  def sockets_=(sockets: Set[SocketConnection]) { _sockets = sockets }
  def sockets = _sockets

  def users_=(users: List[UserModel]) { _users = sortUsers(users) }
  def users = _users

  def invites_=(invites: List[InviteModel]) { _invites = invites }
  def invites = _invites

  def events_=(events: List[EventModel]) { _events = events }
  def events = _events

  def reads_=(reads: Map[UUID, Long]) { _reads = reads }
  def reads = _reads

}
