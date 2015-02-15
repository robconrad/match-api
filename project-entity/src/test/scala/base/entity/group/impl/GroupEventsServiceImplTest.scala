/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 1:33 PM
 */

package base.entity.group.impl

import base.common.random.RandomService
import base.common.service.Services
import base.common.time.mock.TimeServiceConstantMock
import base.entity.event.EventTypes
import base.entity.event.model.impl.EventModelImpl
import base.entity.group.kv.GroupEventsKey
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

  private val groupId = RandomService().uuid
  private val body = "some event body"

  private lazy val event = EventModelImpl(
    id = RandomService().uuid,
    groupId = groupId,
    userId = None,
    `type` = EventTypes.JOIN,
    body = body)

  private val groupKey = make[GroupEventsKey](groupId)

  override def beforeAll() {
    super.beforeAll()
    Services.register(TimeServiceConstantMock)
  }

  private def set(length: Int, createIfNotExists: Boolean = false): Unit = {
    assert(service.setEvent(event, createIfNotExists).await() == Right(event))
    assert(groupKey.lLen.await() == length)
  }

  private def mSet(length: Int*) = length.foreach(set(_))

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
