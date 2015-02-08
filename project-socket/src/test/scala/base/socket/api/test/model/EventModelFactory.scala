/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 2:29 PM
 */

package base.socket.api.test.model

import java.util.UUID

import base.entity.event.EventTypes
import base.entity.event.model.EventModel
import base.entity.event.model.impl.EventModelImpl
import base.socket.api.test.SocketConnection

/**
 * {{ Describe the high level purpose of EventModelFactory here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object EventModelFactory {

  val welcomeBody = "Welcome to Scandal.ly chat! (hush, Michi)"
  val joinBody = "A user joined Scandal.ly chat! (hush, Michi)"
  val messageBody = "a message!"

  def welcome(eventId: UUID, groupId: UUID): EventModel =
    EventModelImpl(eventId, groupId, None, EventTypes.MESSAGE, welcomeBody)

  def join(eventId: UUID, groupId: UUID, socket: SocketConnection): EventModel =
    EventModelImpl(eventId, groupId, Option(socket.props.userId), EventTypes.JOIN, joinBody)

  def message(eventId: UUID, groupId: UUID, socket: SocketConnection): EventModel =
    EventModelImpl(eventId, groupId, Option(socket.props.userId), EventTypes.MESSAGE, messageBody)

  def `match`(eventId: UUID, groupId: UUID, messageBody: String): EventModel =
    EventModelImpl(eventId, groupId, None, EventTypes.MATCH, messageBody)

}
