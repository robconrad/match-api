/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/22/15 6:38 PM
 */

package base.entity.event.impl

import base.common.random.RandomService
import base.common.service.Services
import base.common.time.mock.TimeServiceMonotonicMock
import base.entity.auth.context.ChannelContextDataFactory
import base.entity.command.impl.CommandServiceImplTest
import base.entity.command.model.CommandModel
import base.entity.event.model.AckEventsModel
import base.entity.event.model.impl.{ AckEventsResponseModelImpl, AckEventsModelImpl }
import base.entity.group.kv.{ GroupKey, GroupUserKey }
import base.entity.group.model.impl.GroupModelImpl

/**
 * Responsible for testing the logic and side effects of the Impl
 * (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class AckEventsCommandServiceImplTest extends CommandServiceImplTest[AckEventsModel] {

  val service = new AckEventsCommandServiceImpl()

  private val groupId = RandomService().uuid
  private val eventCount = 11L
  private val lastReadEventCount = 10L

  private implicit val channelCtx = ChannelContextDataFactory.userAuth(groupId)
  implicit val model = AckEventsModelImpl(groupId, lastReadEventCount)

  private val group = GroupModelImpl(groupId, None, None, Option(lastReadEventCount), eventCount)
  private val response = AckEventsResponseModelImpl(group)
  private val groupUserKey = make[GroupUserKey]((groupId, authCtx.userId))
  private val groupKey = make[GroupKey](groupId)

  test("success") {
    assert(groupKey.setEventCount(eventCount).await())
    assert(groupUserKey.getLastReadEventCount.await() == None)

    assert(service.innerExecute(model).await() == Right(response))
    assert(groupUserKey.getLastReadEventCount.await() == Option(lastReadEventCount))

    val response2 = response.copy(group = group.copy(lastReadEventCount = Option(lastReadEventCount + 1)))
    assert(service.innerExecute(model.copy(lastReadEventCount = lastReadEventCount + 1)).await() == Right(response2))
    assert(groupUserKey.getLastReadEventCount.await() == Option(lastReadEventCount + 1))
  }

}
