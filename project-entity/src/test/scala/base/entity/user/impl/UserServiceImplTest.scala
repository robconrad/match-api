/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 7:28 PM
 */

package base.entity.user.impl

import java.util.UUID

import base.common.random.RandomService
import base.common.service.TestServices
import base.entity.auth.context.{ ChannelContext, ChannelContextDataFactory }
import base.entity.error.ApiErrorService
import base.entity.group.GroupService
import base.entity.group.model.impl.GroupModelImpl
import base.entity.kv.Key._
import base.entity.kv.{ScredisKeyFactoryService, KvTest}
import base.entity.service.EntityServiceTest
import base.entity.user.impl.UserServiceImpl.Errors
import base.entity.user.kv._
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

  private val name = "bob"
  private val userId = RandomService().uuid
  private val userId1 = RandomService().uuid
  private val userId2 = RandomService().uuid
  private val groupId1 = RandomService().uuid
  private val groupId2 = RandomService().uuid

  private implicit val channelCtx = ChannelContextDataFactory.userAuth

  test("getUser") {
    val key = mock[UserKey]
    key.getNameAttributes _ expects () returning Future.successful(UserNameAttributes(None, Option(name)))
    val model = UserModel(userId, None, Option(name))
    assert(service.getUser(userId, key).await() == Right(model))
  }

  test("getUsers") {
    val userIds = List(userId1, userId2)

    val model1 = UserModel(userId1, None, Option(name))
    val model2 = UserModel(userId2, None, None)
    val models = List(model1, model2)

    val (key1, key2) = (mock[UserKey], mock[UserKey])
    key1.getNameAttributes _ expects () returning Future.successful(UserNameAttributes(None, Option(name)))
    key2.getNameAttributes _ expects () returning Future.successful(UserNameAttributes(None, None))

    val makeService = mock[ScredisKeyFactoryService]
    (makeService.make[UserKey](_: Any)(_: Manifest[UserKey])) expects (*, *) returning key1
    (makeService.make[UserKey](_: Any)(_: Manifest[UserKey])) expects (*, *) returning key2

    val unregister = TestServices.register(makeService)
    assert(service.getUsersFromKey(authCtx.userId, userIds).await() == Right(models))
    unregister()
  }

  test("getGroups - success") {
    val groups = Set(groupId1, groupId2)
    val group1 = GroupModelImpl(groupId1, List(), List(), None, None, eventCount = 0)
    val group2 = group1.copy(id = groupId2)
    val key = mock[UserGroupsKey]
    key.members _ expects () returning Future.successful(groups)
    val groupService = mock[GroupService]
    (groupService.getGroup(_: UUID, _: UUID)(_: Pipeline, _: ChannelContext)) expects
      (*, *, *, *) returning Future.successful(Right(Option(group1)))
    (groupService.getGroup(_: UUID, _: UUID)(_: Pipeline, _: ChannelContext)) expects
      (*, *, *, *) returning Future.successful(Right(Option(group2)))
    val unregister = TestServices.register(groupService)
    assert(service.getGroupsFromKey(userId, key).await() == Right(List(group1, group2)))
    unregister()
  }

  test("getGroups - GroupService ApiError") {
    val groups = Set(groupId1, groupId2)
    val key = mock[UserGroupsKey]
    key.members _ expects () returning Future.successful(groups)
    val error = ApiErrorService().badRequest("whatever")
    val groupService = mock[GroupService]
    (groupService.getGroup(_: UUID, _: UUID)(_: Pipeline, _: ChannelContext)) expects
      (*, *, *, *) returning Future.successful(Left(error)) twice ()
    val unregister = TestServices.register(groupService)
    assert(service.getGroupsFromKey(userId, key).await() == Left(error))
    unregister()
  }

  test("getGroups - not all groups returned") {
    val groups = Set(groupId1, groupId2)
    val group = GroupModelImpl(groupId1, List(), List(), None, None, eventCount = 0)
    val key = mock[UserGroupsKey]
    key.members _ expects () returning Future.successful(groups)
    val groupService = mock[GroupService]
    (groupService.getGroup(_: UUID, _: UUID)(_: Pipeline, _: ChannelContext)) expects
      (*, *, *, *) returning Future.successful(Right(Option(group))) twice ()
    val unregister = TestServices.register(groupService)
    assert(service.getGroupsFromKey(userId, key).await() == Errors.notAllGroupsReturned)
    unregister()
  }

  test("getPendingGroups - success") {
    val groups = Set(groupId1, groupId2)
    val group1 = GroupModelImpl(groupId1, List(), List(), None, None, eventCount = 0)
    val group2 = group1.copy(id = groupId2)
    val key = mock[UserGroupsInvitedKey]
    key.members _ expects () returning Future.successful(groups)
    val groupService = mock[GroupService]
    (groupService.getGroup(_: UUID, _: UUID)(_: Pipeline, _: ChannelContext)) expects
      (*, *, *, *) returning Future.successful(Right(Option(group1)))
    (groupService.getGroup(_: UUID, _: UUID)(_: Pipeline, _: ChannelContext)) expects
      (*, *, *, *) returning Future.successful(Right(Option(group2)))
    val unregister = TestServices.register(groupService)
    assert(service.getPendingGroupsFromKey(userId, key).await() == Right(List(group1, group2)))
    unregister()
  }

}
