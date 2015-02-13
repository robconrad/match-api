/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/12/15 8:49 PM
 */

package base.entity.group.impl

import java.util.UUID

import base.common.random.RandomService
import base.common.service.{ Services, TestServices }
import base.common.time.mock.TimeServiceConstantMock
import base.entity.auth.context.{ ChannelContext, ChannelContextDataFactory }
import base.entity.error.ApiErrorService
import base.entity.group.kv.{ GroupKey, GroupPhonesInvitedKey, GroupUserKey }
import base.entity.group.model.impl.{ GroupModelBuilder, GroupModelImpl, InviteModelImpl }
import base.entity.kv.KvTest
import base.entity.service.EntityServiceTest
import base.entity.user.UserService
import base.entity.user.kv._
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
  private val group = GroupModelImpl(groupId, users, List(), Option(time), Option(time), eventCount)

  private val method = new service.GetGroupMethod(channelCtx.authCtx.userId, groupId)

  override def beforeAll() {
    super.beforeAll()
    Services.register(TimeServiceConstantMock)
  }

  test("getGroup - no invites - success") {
    val userService = mock[UserService]
    (userService.getUsers(_: UUID, _: List[UUID])(_: ChannelContext)) expects
      (*, *, *) returning Future.successful(Right(users))
    (userService.getUsers(_: UUID, _: List[UUID])(_: ChannelContext)) expects
      (*, *, *) returning Future.successful(Right(List()))
    val unregister = TestServices.register(userService)

    assert(make[GroupUserKey](groupId, channelCtx.authCtx.userId).setLastRead(time).await())
    assert(make[GroupKey](groupId).setLastEvent(time).await())
    assert(make[GroupKey](groupId).setEventCount(eventCount).await())

    assert(service.getGroup(authCtx.userId, groupId).await() == Right(Option(group)))

    unregister()
  }

  test("getGroup - phone invites - success") {
    val userService = mock[UserService]
    (userService.getUsers(_: UUID, _: List[UUID])(_: ChannelContext)) expects
      (*, *, *) returning Future.successful(Right(users))
    (userService.getUsers(_: UUID, _: List[UUID])(_: ChannelContext)) expects
      (*, *, *) returning Future.successful(Right(List()))
    val unregister = TestServices.register(userService)

    val phone1 = "555-1234"
    val phone2 = "555-1235"
    val label = "bob"

    val expectedGroup = group.copy(invites = List(
      InviteModelImpl(phone2, None, None),
      InviteModelImpl(phone1, None, Option(label))))

    assert(make[GroupPhonesInvitedKey](groupId).add(phone1, phone2).await() == 2L)
    assert(make[UserPhoneLabelKey](UserPhone(authCtx.userId, phone1)).set(label).await())

    assert(make[GroupUserKey](groupId, channelCtx.authCtx.userId).setLastRead(time).await())
    assert(make[GroupKey](groupId).setLastEvent(time).await())
    assert(make[GroupKey](groupId).setEventCount(eventCount).await())

    val actual = service.getGroup(authCtx.userId, groupId).await()
    val expected = Right(Option(expectedGroup))

    debug(actual.toString)
    debug(expected.toString)

    assert(actual == expected)

    unregister()
  }

  test("getGroup - user invites - success") {
    val userId1 = RandomService().uuid
    val userId2 = RandomService().uuid
    val phone1 = "555-1234"
    val phone2 = "555-1235"
    val label = "bob"
    val invitedUsers = List(UserModel(userId1, None, Option(label)), UserModel(userId2, None, None))
    val userService = mock[UserService]
    (userService.getUsers(_: UUID, _: List[UUID])(_: ChannelContext)) expects
      (*, *, *) returning Future.successful(Right(users))
    (userService.getUsers(_: UUID, _: List[UUID])(_: ChannelContext)) expects
      (*, *, *) returning Future.successful(Right(invitedUsers))
    val unregister = TestServices.register(userService)

    val expectedGroup = group.copy(invites = List(
      InviteModelImpl(phone1, None, Option(label)),
      InviteModelImpl(phone2, None, None)))

    assert(make[GroupPhonesInvitedKey](groupId).add(phone1, phone2).await() == 2L)
    assert(make[PhoneKey](phone1).set(userId1).await())
    assert(make[PhoneKey](phone2).set(userId2).await())

    assert(make[GroupUserKey](groupId, channelCtx.authCtx.userId).setLastRead(time).await())
    assert(make[GroupKey](groupId).setLastEvent(time).await())
    assert(make[GroupKey](groupId).setEventCount(eventCount).await())

    val actual = service.getGroup(authCtx.userId, groupId).await()
    val expected = Right(Option(expectedGroup))

    debug(actual.toString)
    debug(expected.toString)

    assert(actual == expected)

    unregister()
  }

  test("getGroup - phone & user invites - success") {
    val userId1 = RandomService().uuid
    val phone1 = "555-1234"
    val phone2 = "555-1235"
    val label = "bob"
    val invitedUsers = List(UserModel(userId1, None, Option(label)))
    val userService = mock[UserService]
    (userService.getUsers(_: UUID, _: List[UUID])(_: ChannelContext)) expects
      (*, *, *) returning Future.successful(Right(users))
    (userService.getUsers(_: UUID, _: List[UUID])(_: ChannelContext)) expects
      (*, *, *) returning Future.successful(Right(invitedUsers))
    val unregister = TestServices.register(userService)

    val expectedGroup = group.copy(invites = List(
      InviteModelImpl(phone2, None, Option(label)),
      InviteModelImpl(phone1, None, Option(label))))

    assert(make[GroupPhonesInvitedKey](groupId).add(phone1, phone2).await() == 2L)
    assert(make[PhoneKey](phone1).set(userId1).await())
    assert(make[UserPhoneLabelKey](UserPhone(authCtx.userId, phone2)).set(label).await())

    assert(make[GroupUserKey](groupId, channelCtx.authCtx.userId).setLastRead(time).await())
    assert(make[GroupKey](groupId).setLastEvent(time).await())
    assert(make[GroupKey](groupId).setEventCount(eventCount).await())

    val actual = service.getGroup(authCtx.userId, groupId).await()
    val expected = Right(Option(expectedGroup))

    debug(actual.toString)
    debug(expected.toString)

    assert(actual == expected)

    unregister()
  }

  test("getGroup - users get failed") {
    val error = ApiErrorService().badRequest("whatever")
    val userId = RandomService().uuid
    val userIds = List(userId)
    val builder = GroupModelBuilder()
    val userService = mock[UserService]
    (userService.getUsers(_: UUID, _: List[UUID])(_: ChannelContext)) expects
      (*, *, *) returning Future.successful(Left(error))
    val unregister = TestServices.register(userService)
    assert(method.usersGet(userIds, builder).await() == Left(error))
    unregister()
  }

}
