/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 5:50 PM
 */

package base.entity.group.impl

import base.common.random.RandomService
import base.common.service.Services
import base.common.time.mock.{TimeServiceMonotonicMock, TimeServiceConstantMock}
import base.entity.auth.context.ChannelContextDataFactory
import base.entity.event.EventTypes
import base.entity.event.model.impl.EventModelImpl
import base.entity.group.kv.{GroupUserKey, GroupKey, GroupEventsKey}
import base.entity.kv.KvTest
import base.entity.service.EntityServiceTest

/**
 * {{ Describe the high level purpose of GroupEventsServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupEventsServiceImplTest extends EntityServiceTest with KvTest {

  private val count = 2
  private val store = 3
  private val delta = 2

  val service = new GroupEventsServiceImpl(count, store, delta)

  private implicit val channelCtx = ChannelContextDataFactory.userAuth

  private val groupId = RandomService().uuid
  private val body = "some event body"

  private lazy val event = EventModelImpl(
    id = RandomService().uuid,
    groupId = groupId,
    userId = None,
    `type` = EventTypes.JOIN,
    body = body)

  private val groupEventsKey = make[GroupEventsKey](groupId)
  private val groupKey = make[GroupKey](groupId)
  private var eventCount = 0

  override def beforeAll() {
    super.beforeAll()
    Services.register(TimeServiceConstantMock)
  }

  override def beforeEach() {
    super.beforeEach()
    eventCount = 0
  }

  private def set(length: Long, createIfNotExists: Boolean = false) {
    if (createIfNotExists || length > 0) {
      eventCount += 1
    }
    assert(service.setEvent(event, createIfNotExists).await() == Right(event))
    assert(groupEventsKey.lLen.await() == length)
    length match {
      case 0 => assert(groupKey.getLastEventAndCount.await()._2 == None)
      case n =>
        assert(groupKey.getLastEventAndCount.await()._2 == Option(eventCount))
        assert(groupKey.getLastEventAndCount.await()._1 == Option(TimeServiceConstantMock.now))
    }
  }

  private def mSet(length: Long*) = length.foreach(set(_))

  test("setEvent / getEvents") {
    set(0)
    set(1, createIfNotExists = true)
    mSet(2,3,4)
    assert(service.getEvents(groupId).await() == Right(List(event, event)))
  }

  test("trim events") {
    set(1, createIfNotExists = true)
    mSet(2, 3, 4, 5, 3, 4, 5, 3, 4, 5, 3, 4, 5)
  }

}
