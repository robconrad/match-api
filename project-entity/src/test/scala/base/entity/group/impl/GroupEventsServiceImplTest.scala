/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 7:11 PM
 */

package base.entity.group.impl

import base.common.random.RandomService
import base.common.service.Services
import base.common.time.mock.TimeServiceConstantMock
import base.entity.event.EventTypes
import base.entity.event.model.EventModel
import base.entity.group.impl.GroupEventsServiceImpl.Errors
import base.entity.kv.KvFactoryService
import base.entity.service.EntityServiceTest

/**
 * {{ Describe the high level purpose of GroupEventsServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupEventsServiceImplTest extends EntityServiceTest {

  private val count = 2

  val service = new GroupEventsServiceImpl(count)

  private val groupId = RandomService().uuid
  private val body = "some event body"

  private lazy val event = EventModel(
    id = RandomService().uuid,
    groupId = groupId,
    userId = None,
    `type` = EventTypes.JOIN,
    body = body)

  private implicit val pipeline = KvFactoryService().pipeline

  override def beforeAll() {
    super.beforeAll()
    Services.register(TimeServiceConstantMock)
  }

  test("setEvent / getEvents") {

    assert(service.setEvent(event, createIfNotExists = false).await() == Errors.setEventFailed)

    assert(service.setEvent(event, createIfNotExists = true).await() == Right(event))

    assert(service.setEvent(event, createIfNotExists = false).await() == Right(event))
    // extra event puts us over count limit
    assert(service.setEvent(event, createIfNotExists = false).await() == Right(event))

    assert(service.getEvents(groupId).await() == Right(List(event, event)))

  }

}
