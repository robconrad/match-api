/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:12 PM
 */

package base.entity.event.impl

import base.common.random.RandomService
import base.common.service.Services
import base.common.time.mock.TimeServiceMonotonicMock
import base.entity.auth.context.ChannelContextDataFactory
import base.entity.command.impl.CommandServiceImplTest
import base.entity.event.model.AckEventsModel
import base.entity.event.model.impl.AckEventsModelImpl
import base.entity.group.kv.GroupUserKey

/**
 * Responsible for testing the logic and side effects of the Impl
 * (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class AckEventsCommandServiceImplTest extends CommandServiceImplTest[AckEventsModel] {

  val service = new AckEventsCommandServiceImpl()

  private val groupId = RandomService().uuid

  private implicit val channelCtx = ChannelContextDataFactory.userAuth(groupId)
  implicit val model = AckEventsModelImpl(groupId)

  private val groupUserKey = make[GroupUserKey]((groupId, authCtx.userId))

  private val timeMock = TimeServiceMonotonicMock

  override def beforeAll() {
    super.beforeAll()
    Services.register(timeMock)
  }

  test("success") {
    assert(groupUserKey.getLastRead.await() == None)

    assert(service.execute(model).await() == None)
    assert(groupUserKey.getLastRead.await() == Option(timeMock.nowNoUpdate))

    val now = timeMock.nowNoUpdate
    assert(service.execute(model).await() == None)
    assert(groupUserKey.getLastRead.await() == Option(timeMock.nowNoUpdate))
    assert(now != timeMock.nowNoUpdate)
  }

}
