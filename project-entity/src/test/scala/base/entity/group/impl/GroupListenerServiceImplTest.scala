/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/24/15 11:46 PM
 */

package base.entity.group.impl

import akka.testkit.TestProbe
import base.common.random.RandomService
import base.entity.auth.context.ChannelContextDataFactory
import base.entity.command.model.CommandModel
import base.entity.event.EventTypes
import base.entity.event.model.EventModel
import base.entity.group.EventCommandService
import base.entity.group.impl.GroupListenerActor.{ Publish, Unregister, Register }
import base.entity.message.MessageCommandService
import base.entity.service.EntityServiceTest

/**
 * {{ Describe the high level purpose of GroupServiceImplTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupListenerServiceImplTest extends EntityServiceTest {

  private val probe = TestProbe()

  private def expect[T](m: T) = probe.expectMsg(probeTimeout, m)

  val service = new GroupListenerServiceImpl(probe.ref)

  implicit val channelCtx = ChannelContextDataFactory.userAuth

  test("register") {
    val groupIds = List.fill(3)(RandomService().uuid).toSet
    service.register(authCtx.userId, groupIds)
    expect(Register(groupIds, authCtx.userId, channelCtx.pushChannel.get))
  }

  test("unregister") {
    service.unregister(authCtx.userId)
    expect(Unregister(channelCtx.authCtx.user.get.id))
  }

  test("publish") {
    val event = EventModel(RandomService().uuid, RandomService().uuid, None, EventTypes.MESSAGE, "")
    val command = CommandModel(EventCommandService.outCmd.get, event)
    service.publish(command)
    expect(Publish(command))
  }

}
