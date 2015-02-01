/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 8:57 AM
 */

package base.entity.group.impl

import java.util.UUID

import base.common.random.RandomService
import base.common.service.{Services, TestServices}
import base.common.time.mock.TimeServiceConstantMock
import base.entity.auth.context.{ChannelContext, ChannelContextDataFactory}
import base.entity.error.ApiErrorService
import base.entity.group.kv.{GroupKeyService, GroupUserKeyService}
import base.entity.group.model.impl.{GroupModelBuilder, GroupModelImpl}
import base.entity.kv.Key._
import base.entity.kv.KvTest
import base.entity.service.EntityServiceTest
import base.entity.user.UserService
import base.entity.user.model.UserModel

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of GroupServiceImplTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupServiceImplTest extends EntityServiceTest with KvTest {

  val service = new GroupServiceImpl()

  private implicit val channelCtx = ChannelContextDataFactory.userAuth

  private val time = TimeServiceConstantMock.now

  private val users = List[UserModel]()
  private val eventCount = 101
  private val groupId = RandomService().uuid
  private val group = GroupModelImpl(groupId, users, Option(time), Option(time), eventCount)

  private val method = new service.GetGroupMethod(channelCtx.authCtx.userId, groupId)

  override def beforeAll() {
    super.beforeAll()
    Services.register(TimeServiceConstantMock)
  }

  test("getGroup - success") {
    val userService = mock[UserService]
    (userService.getUsers(_: UUID, _: List[UUID])(_: Pipeline, _: ChannelContext)) expects
      (*, *, *, *) returning Future.successful(Right(users))
    val unregister = TestServices.register(userService)

    assert(GroupUserKeyService().make(groupId, channelCtx.authCtx.userId).setLastRead(time).await())
    assert(GroupKeyService().make(groupId).setLastEvent(time).await())
    assert(GroupKeyService().make(groupId).setEventCount(eventCount).await())

    assert(service.getGroup(authCtx.userId, groupId).await() == Right(Option(group)))

    unregister()
  }

  test("getGroup - users get failed") {
    val error = ApiErrorService().badRequest("whatever")
    val userId = RandomService().uuid
    val userIds = List(userId)
    val builder = GroupModelBuilder()
    val userService = mock[UserService]
    (userService.getUsers(_: UUID, _: List[UUID])(_: Pipeline, _: ChannelContext)) expects
      (*, *, *, *) returning Future.successful(Left(error))
    val unregister = TestServices.register(userService)
    assert(method.usersGet(userIds, builder).await() == Left(error))
    unregister()
  }

}
