/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 1:02 PM
 */

package base.entity.user.impl

import java.util.UUID

import base.common.random.RandomService
import base.common.service.TestServices
import base.entity.auth.context.{ AuthContext, AuthContextDataFactory }
import base.entity.error.ApiError
import base.entity.group.GroupService
import base.entity.group.model.GroupModel
import base.entity.kv.Key._
import base.entity.kv.KvTest
import base.entity.service.EntityServiceTest
import base.entity.user.impl.UserServiceImpl.Errors
import base.entity.user.kv.{ UserGroupsKey, UserUserLabelKey, UserUserLabelKeyService }
import base.entity.user.model.UserModel

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of UserServiceImplTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class UserServiceImplTest extends EntityServiceTest with KvTest {

  val service = new UserServiceImpl()

  private val label = "bob"
  private val userId = RandomService().uuid
  private val userId1 = RandomService().uuid
  private val userId2 = RandomService().uuid
  private val groupId1 = RandomService().uuid
  private val groupId2 = RandomService().uuid

  private implicit val authCtx = AuthContextDataFactory.userAuth

  test("getUser") {
    val key = mock[UserUserLabelKey]
    key.get _ expects () returning Future.successful(Option(label))
    val model = UserModel(userId, Option(label))
    assert(service.getUser(userId, key).await() == Right(model))
  }

  test("getUsers") {
    val userIds = List(userId1, userId2)

    val model1 = UserModel(userId1, Option(label))
    val model2 = UserModel(userId2, None)
    val models = List(model1, model2)

    val (key1, key2) = (mock[UserUserLabelKey], mock[UserUserLabelKey])
    key1.get _ expects () returning Future.successful(Option(label))
    key2.get _ expects () returning Future.successful(None)

    val keyService = mock[UserUserLabelKeyService]
    (keyService.make(_: (UUID, UUID))(_: Pipeline)) expects (*, *) returning key1
    (keyService.make(_: (UUID, UUID))(_: Pipeline)) expects (*, *) returning key2

    assert(service.getUsers(userIds, keyService).await() == Right(models))
  }

  test("getGroups - success") {
    val groups = Set(groupId1, groupId2)
    val group1 = GroupModel(groupId1, List(), None, None, eventCount = 0)
    val group2 = group1.copy(id = groupId2)
    val key = mock[UserGroupsKey]
    key.members _ expects () returning Future.successful(groups)
    val groupService = mock[GroupService]
    (groupService.getGroup(_: UUID)(_: Pipeline, _: AuthContext)) expects
      (*, *, *) returning Future.successful(Right(Option(group1)))
    (groupService.getGroup(_: UUID)(_: Pipeline, _: AuthContext)) expects
      (*, *, *) returning Future.successful(Right(Option(group2)))
    val unregister = TestServices.register(groupService)
    assert(service.getGroups(userId, key).await() == Right(List(group1, group2)))
    unregister()
  }

  test("getGroups - GroupService ApiError") {
    val groups = Set(groupId1, groupId2)
    val key = mock[UserGroupsKey]
    key.members _ expects () returning Future.successful(groups)
    val error = ApiError("whatever")
    val groupService = mock[GroupService]
    (groupService.getGroup(_: UUID)(_: Pipeline, _: AuthContext)) expects
      (*, *, *) returning Future.successful(Left(error)) twice ()
    val unregister = TestServices.register(groupService)
    assert(service.getGroups(userId, key).await() == Left(error))
    unregister()
  }

  test("getGroups - not all groups returned") {
    val groups = Set(groupId1, groupId2)
    val group = GroupModel(groupId1, List(), None, None, eventCount = 0)
    val key = mock[UserGroupsKey]
    key.members _ expects () returning Future.successful(groups)
    val groupService = mock[GroupService]
    (groupService.getGroup(_: UUID)(_: Pipeline, _: AuthContext)) expects
      (*, *, *) returning Future.successful(Right(Option(group))) twice ()
    val unregister = TestServices.register(groupService)
    assert(service.getGroups(userId, key).await() == Errors.notAllGroupsReturned)
    unregister()
  }

}
