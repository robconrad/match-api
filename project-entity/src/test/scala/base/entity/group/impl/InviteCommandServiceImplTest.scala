/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:16 PM
 */

package base.entity.group.impl

import java.util.UUID

import base.common.random.RandomService
import base.common.random.mock.RandomServiceMock
import base.common.service.{ Services, TestServices }
import base.common.time.mock.TimeServiceConstantMock
import base.entity.auth.context.AuthContextDataFactory
import base.entity.command.impl.CommandServiceImplTest
import base.entity.error.ApiError
import base.entity.group.impl.InviteCommandServiceImpl.Errors
import base.entity.group.kv.impl.{ GroupKeyImpl, GroupPairKeyImpl, GroupUsersKeyImpl }
import base.entity.group.kv.{ GroupPairKeyService, GroupUsersKeyService }
import base.entity.group.mock.{ GroupEventsServiceMock, GroupServiceMock }
import base.entity.group.model.{ GroupModel, InviteModel, InviteResponseModel }
import base.entity.kv.Key._
import base.entity.kv.KeyProps.{ CreatedProp, UpdatedProp }
import base.entity.kv.impl.PrivateHashKeyImpl
import base.entity.kv.mock.{ KeyLoggerMock, PrivateHashKeyMock, StringKeyMock }
import base.entity.kv.{ KeyId, KvFactoryService }
import base.entity.user.kv.UserGroupsKeyService
import base.entity.user.kv.UserKeyProps.UserIdProp
import base.entity.user.kv.impl.{ UserUserLabelKeyImpl, PhoneKeyImpl, UserGroupsKeyImpl, UserKeyImpl }
import base.entity.user.model.UserModel

import scala.concurrent.Future

/**
 * Responsible for testing the logic and side effects of the Impl
 * (i.e. validation, persistence, etc.)
 * @author rconrad
 */
// scalastyle:off null
class InviteCommandServiceImplTest extends CommandServiceImplTest {

  val service = new InviteCommandServiceImpl("welcome!")

  private val phone = "555-1234"
  private val label = "Bob"
  private val time = TimeServiceConstantMock.now

  private val userId = RandomService().uuid
  private val groupId = RandomService().uuid

  private val error = ApiError("test")

  private val randomMock = new RandomServiceMock()
  private val groupMock = new GroupServiceMock(getGroupResult = Future.successful(Right(None)))
  private val groupEventsMock = new GroupEventsServiceMock()

  private implicit val authCtx = AuthContextDataFactory.userAuth
  private implicit val model = InviteModel(phone, label)

  override def beforeAll() {
    super.beforeAll()
    Services.register(TimeServiceConstantMock)
    Services.register(randomMock)
    Services.register(groupMock)
    Services.register(groupEventsMock)
  }

  private def command(implicit input: InviteModel) = new service.InviteCommand(input)

  test("without perms") {
    assertPermException(authCtx => {
      service.execute(model)(authCtx)
    })
  }

  test("success - new user") {
    val eventCount = 0
    val userId = randomMock.nextUuid()
    val users = List(UserModel(userId, Option(label)))
    val groupId = randomMock.nextUuid(1)
    val group = GroupModel(groupId, users, Option(time), Option(time), eventCount)
    val groupMock = new GroupServiceMock(getGroupResult = Future.successful(Right(Option(group))))
    val unregister = TestServices.register(groupMock)
    val response = InviteResponseModel(userId, group)
    assert(service.innerExecute(model).await() == Right(response))

    val phoneKey = new PrivateHashKeyImpl(s"phone-$phone", KeyLoggerMock)
    assert(phoneKey.getDateTime(CreatedProp).await().exists(_.isEqual(time)))
    assert(phoneKey.getDateTime(UpdatedProp).await().exists(_.isEqual(time)))
    assert(phoneKey.getId(UserIdProp).await().contains(userId))

    val userKey = new PrivateHashKeyImpl(s"user-$userId", KeyLoggerMock)
    assert(userKey.getDateTime(CreatedProp).await().exists(_.isEqual(time)))

    val userUserLabelKey = new UserUserLabelKeyImpl(s"userUserLabel-${authCtx.userId}-$userId", KeyLoggerMock)
    assert(userUserLabelKey.get.await().contains(label))

    val groupKey = new PrivateHashKeyImpl(s"group-$groupId", KeyLoggerMock)
    assert(groupKey.getDateTime(CreatedProp).await().exists(_.isEqual(time)))

    val groupPairKey = GroupPairKeyService().make(userId, authCtx.userId)
    assert(groupPairKey.get.await().contains(groupId))

    val groupUsersKey = GroupUsersKeyService().make(KeyId(groupId))
    assert(groupUsersKey.isMember(userId).await())
    assert(groupUsersKey.isMember(authCtx.userId).await())

    val userGroupsKeyA = UserGroupsKeyService().make(KeyId(userId))
    assert(userGroupsKeyA.isMember(groupId).await())

    val userGroupsKeyB = UserGroupsKeyService().make(KeyId(authCtx.userId))
    assert(userGroupsKeyB.isMember(groupId).await())

    unregister()
  }

  test("success - user exists") {
    val phoneKey = new PrivateHashKeyImpl(s"phone-$phone", KeyLoggerMock)
    assert(phoneKey.set(UserIdProp, userId).await())

    val eventCount = 0
    val users = List(UserModel(userId, Option(label)))
    val groupId = randomMock.nextUuid()
    val group = GroupModel(groupId, users, Option(time), Option(time), eventCount)
    val groupMock = new GroupServiceMock(getGroupResult = Future.successful(Right(Option(group))))
    val unregister = TestServices.register(groupMock)
    val response = InviteResponseModel(userId, group)
    assert(service.innerExecute(model).await() == Right(response))

    val userUserLabelKey = new UserUserLabelKeyImpl(s"userUserLabel-${authCtx.userId}-$userId", KeyLoggerMock)
    assert(userUserLabelKey.get.await().contains(label))

    val groupKey = new PrivateHashKeyImpl(s"group-$groupId", KeyLoggerMock)
    assert(groupKey.getDateTime(CreatedProp).await().exists(_.isEqual(time)))

    val groupPairKey = GroupPairKeyService().make(userId, authCtx.userId)
    assert(groupPairKey.get.await().contains(groupId))

    val groupUsersKey = GroupUsersKeyService().make(KeyId(groupId))
    assert(groupUsersKey.isMember(userId).await())
    assert(groupUsersKey.isMember(authCtx.userId).await())

    val userGroupsKeyA = UserGroupsKeyService().make(KeyId(userId))
    assert(userGroupsKeyA.isMember(groupId).await())

    val userGroupsKeyB = UserGroupsKeyService().make(KeyId(authCtx.userId))
    assert(userGroupsKeyB.isMember(groupId).await())

    unregister()
  }

  test("success - group exists") {
    val phoneKey = new PrivateHashKeyImpl(s"phone-$phone", KeyLoggerMock)
    assert(phoneKey.set(UserIdProp, userId).await())

    val groupPairKey = GroupPairKeyService().make(userId, authCtx.userId)
    assert(groupPairKey.set(groupId).await())

    val eventCount = 0
    val users = List(UserModel(userId, Option(label)))
    val group = GroupModel(groupId, users, Option(time), Option(time), eventCount)
    val groupMock = new GroupServiceMock(getGroupResult = Future.successful(Right(Option(group))))
    val unregister = TestServices.register(groupMock)
    val response = InviteResponseModel(userId, group)
    assert(service.innerExecute(model).await() == Right(response))

    unregister()
  }

  test("user create failed") {
    val userKey = new UserKeyImpl(new PrivateHashKeyMock(setNxResult = Future.successful(false)))
    val phoneKey = new PhoneKeyImpl(new PrivateHashKeyMock())
    assert(command.userCreate(userId, userKey, phoneKey).await() == Errors.userCreateFailed.await())
  }

  test("phone set userId failed") {
    val phoneKey = new PhoneKeyImpl(new PrivateHashKeyMock(setMultiResult = Future.successful(false)))
    assert(command.phoneSetUserId(userId, phoneKey).await() == Errors.phoneSetUserIdFailed.await())
  }

  test("user user label set failed") {
    val key = new StringKeyMock(setResult = Future.successful(false))
    assert(command.userUserLabelSet(userId, key).await() == Errors.userUserLabelSetFailed.await())
  }

  test("group pair set failed") {
    val groupKey = new GroupKeyImpl(new PrivateHashKeyMock())
    val pairKey = new GroupPairKeyImpl("", KeyLoggerMock) {
      override def set(v: UUID) = Future.successful(false)
    }
    assert(command.groupPairSet(userId, groupId, groupKey, pairKey).await() == Errors.pairSetFailed.await())
  }

  test("group user add failed") {
    val key = new GroupUsersKeyImpl("", KeyLoggerMock) {
      override def add(value: Any*) = Future.successful(0)
    }
    assert(command.groupUsersAdd(userId, groupId, key).await() == Errors.groupUsersAddFailed.await())
  }

  test("invited user groups add failed") {
    val key = new UserGroupsKeyImpl("", KeyLoggerMock) {
      override def add(value: Any*) = Future.successful(0)
    }
    assert(command.invitedUserGroupsAdd(userId, groupId, key).await() == Errors.userGroupsAddFailed.await())
  }

  test("inviting user groups add failed") {
    val key = new UserGroupsKeyImpl("", KeyLoggerMock) {
      override def add(value: Any*) = Future.successful(0)
    }
    assert(command.invitingUserGroupsAdd(userId, groupId, key).await() == Errors.userGroupsAddFailed.await())
  }

  test("group events prepend returned error") {
    val unregister = TestServices.register(new GroupServiceMock(getGroupResult = Future.successful(Left(error))))
    assert(command.groupGet(userId, groupId).await() == Left(error))
    unregister()
  }

  test("group get returned error") {
    val unregister = TestServices.register(
      new GroupEventsServiceMock(setEventResult = Option(Future.successful(Left(error)))))
    assert(command.groupEventsPrepend(userId, groupId).await() == Left(error))
    unregister()
  }

  test("group get failed") {
    val unregister = TestServices.register(new GroupServiceMock(getGroupResult = Future.successful(Right(None))))
    assert(command.groupGet(userId, groupId).await() == Errors.groupGetFailed.await())
    unregister()
  }

}
