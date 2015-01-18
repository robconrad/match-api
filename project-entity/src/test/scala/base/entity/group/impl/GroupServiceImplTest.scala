/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 6:17 PM
 */

package base.entity.group.impl

import base.common.random.RandomService
import base.common.service.{ TestServices, Services }
import base.common.time.mock.TimeServiceConstantMock
import base.entity.auth.context.AuthContextDataFactory
import base.entity.error.ApiError
import base.entity.group.kv.{ GroupKeyService, GroupUserKeyService }
import base.entity.group.model.{ GroupModelBuilder, GroupModel }
import base.entity.kv.{ KeyId, KvTest }
import base.entity.service.EntityServiceTest
import base.entity.user.mock.UserServiceMock
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

  private val userMock = new UserServiceMock(getUsersResult = Future.successful(Right(users)))

  private val method = new service.GetGroupMethod(groupId)

  override def beforeAll() {
    super.beforeAll()
    Services.register(TimeServiceConstantMock)
    Services.register(userMock)
  }

  test("getGroup - success") {
    assert(GroupUserKeyService().make(groupId, authCtx.userId).setLastRead(time).await())
    assert(GroupKeyService().make(KeyId(groupId)).setLastEvent(time).await())
    assert(GroupKeyService().make(KeyId(groupId)).setEventCount(eventCount).await())

    assert(service.getGroup(groupId).await() == Right(Option(group)))
  }

  test("getGroup - users get failed") {
    val error = ApiError("whatever")
    val userId = RandomService().uuid
    val userIds = List(userId)
    val builder = GroupModelBuilder()
    val unregister = TestServices.register(new UserServiceMock(getUsersResult = Future.successful(Left(error))))
    assert(method.usersGet(userIds, builder).await() == Left(error))
    unregister()
  }

}
