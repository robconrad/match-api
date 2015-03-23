/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/22/15 7:28 PM
 */

package base.entity.group.impl

import akka.actor.{ ActorRef, Props }
import akka.pattern.ask
import akka.testkit.TestActorRef
import base.common.logging.Loggable
import base.common.random.RandomService
import base.common.test.ActorTestHelper
import base.entity.auth.context.PushChannel
import base.entity.command.model.CommandModel
import base.entity.event.EventTypes
import base.entity.event.model.EventModel
import base.entity.event.model.impl.EventModelImpl
import base.entity.group.impl.GroupListenerActor.{ Publish, Register, Unregister }
import base.entity.group.model.impl.GroupModelImpl
import base.entity.test.EntityBaseSuite

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of GroupListenerActorTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupListenerActorTest extends EntityBaseSuite with ActorTestHelper with Loggable {

  def register(implicit actor: ActorRef) = {
    val channel = mock[PushChannel]
    val userId = RandomService().uuid
    val groupId = RandomService().uuid
    val event: EventModel = EventModelImpl(RandomService().uuid, None, groupId, None, EventTypes.MESSAGE, "")
    val command = CommandModel(event)

    actor ? Register(Set(groupId), userId, channel) await ()

    (userId, channel, command)
  }

  test("register / publish / unregister") {
    implicit val actor = TestActorRef(Props(new GroupListenerActor()))
    type T = CommandModel[EventModel]

    val n = 10
    val channels = List.fill(n)(register) map {
      case (userId, channel, command) =>
        val event = command.body
        val group = GroupModelImpl(event.groupId, None, None, None, 0L)
        val newEvent: EventModel = EventModelImpl(
          event.id, Option(group), event.groupId, event.userId, event.`type`, event.body, event.time)
        val hydratedCommand = command.copy(body = newEvent)

        // expect user to receive each command twice
        (channel.push[T](_: T)(_: Manifest[T])) expects (hydratedCommand, *) returning Future.successful(true)
        (channel.push[T](_: T)(_: Manifest[T])) expects (hydratedCommand, *) returning Future.successful(true)
        // send the command the first time
        actor ? Publish(command) await ()
        (userId, channel, command)
    }

    channels.foreach {
      case (userId, channel, command) =>
        // cycle through and send it again
        actor ? Publish(command) await ()
        // unregister means we won't receive it
        actor ? Unregister(userId) await ()
        // third time is ignored since user has unregistered
        actor ? Publish(command) await ()
    }
  }

}
