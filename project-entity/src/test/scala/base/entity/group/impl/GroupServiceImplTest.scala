/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 12:59 PM
 */

package base.entity.group.impl

import java.util.UUID

import base.common.random.RandomService
import base.common.service.{ Services, TestServices }
import base.common.time.mock.TimeServiceConstantMock
import base.entity.auth.context.{ AuthContext, AuthContextDataFactory }
import base.entity.error.ApiError
import base.entity.group.UserService
import base.entity.group.kv.{ GroupKeyService, GroupUserKeyService }
import base.entity.group.model.{ GroupModel, GroupModelBuilder }
import base.entity.kv.Key._
import base.entity.kv.KvTest
import base.entity.service.EntityServiceTest
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

  private implicit val authCtx = AuthContextDataFactory.userAuth

  private val time = TimeServiceConstantMock.now

  private val users = List[UserModel]()
  private val eventCount = 101
  private val groupId = RandomService().uuid
  private val group = GroupModel(groupId, users, Option(time), Option(time), eventCount)

  private val method = new service.GetGroupMethod(groupId)

  override def beforeAll() {
    super.beforeAll()
    Services.register(TimeServiceConstantMock)
  }

  test("getGroup - success") {
    val userService = mock[UserService]
    (userService.getUsers(_: List[UUID])(_: Pipeline, _: AuthContext)) expects
      (*, *, *) returning Future.successful(Right(users))
    val unregister = TestServices.register(userService)

    assert(GroupUserKeyService().make(groupId, authCtx.userId).setLastRead(time).await())
    assert(GroupKeyService().make(groupId).setLastEvent(time).await())
    assert(GroupKeyService().make(groupId).setEventCount(eventCount).await())

    assert(service.getGroup(groupId).await() == Right(Option(group)))

    unregister()
  }

  test("getGroup - users get failed") {
    val error = ApiError("whatever")
    val userId = RandomService().uuid
    val userIds = List(userId)
    val builder = GroupModelBuilder()
    val userService = mock[UserService]
    (userService.getUsers(_: List[UUID])(_: Pipeline, _: AuthContext)) expects
      (*, *, *) returning Future.successful(Left(error))
    val unregister = TestServices.register(userService)
    assert(method.usersGet(userIds, builder).await() == Left(error))
    unregister()
  }

}
