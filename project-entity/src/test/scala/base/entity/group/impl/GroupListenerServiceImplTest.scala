/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 11:38 AM
 */

package base.entity.group.impl

import akka.testkit.TestProbe
import base.common.random.RandomService
import base.entity.auth.context.ChannelContextDataFactory
import base.entity.command.model.CommandModel
import base.entity.event.model.EventModel
import base.entity.group.impl.GroupListenerActor.{ Publish, Register, Unregister }
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
    val event = mock[EventModel]
    val command = CommandModel(event)
    service.publish(command)
    expect(Publish(command))
  }

}
