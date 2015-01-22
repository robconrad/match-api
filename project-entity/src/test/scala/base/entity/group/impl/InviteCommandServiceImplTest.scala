/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 2:41 PM
 */

package base.entity.group.impl

import java.util.UUID

import base.common.random.RandomService
import base.common.random.mock.RandomServiceMock
import base.common.service.{ Services, TestServices }
import base.common.time.mock.TimeServiceConstantMock
import base.entity.auth.context.{ AuthContext, AuthContextDataFactory }
import base.entity.command.impl.CommandServiceImplTest
import base.entity.error.ApiError
import base.entity.event.model.EventModel
import base.entity.group.impl.InviteCommandServiceImpl.Errors
import base.entity.group.kv._
import base.entity.group.model.{ GroupModel, InviteModel, InviteResponseModel }
import base.entity.group.{ GroupEventsService, GroupService }
import base.entity.kv.Key._
import base.entity.user.kv._
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

  private implicit val authCtx = AuthContextDataFactory.userAuth
  private implicit val model = InviteModel(phone, label)

  override def beforeAll() {
    super.beforeAll()
    Services.register(TimeServiceConstantMock)
    Services.register(randomMock)
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
    val groupService = mock[GroupService]
    (groupService.getGroup(_: UUID)(_: Pipeline, _: AuthContext)) expects
      (*, *, *) returning Future.successful(Right(Option(group)))
    val unregister = TestServices.register(groupService)
    val response = InviteResponseModel(userId, group)
    assert(service.innerExecute(model).await() == Right(response))

    val phoneKey = PhoneKeyService().make(phone)
    assert(phoneKey.getCreated.await().exists(_.isEqual(time)))
    assert(phoneKey.getUpdated.await().exists(_.isEqual(time)))
    assert(phoneKey.getUserId.await().contains(userId))

    val userKey = UserKeyService().make(userId)
    assert(userKey.getCreated.await().exists(_.isEqual(time)))

    val userUserLabelKey = UserUserLabelKeyService().make(authCtx.userId, userId)
    assert(userUserLabelKey.get.await().contains(label))

    val groupKey = GroupKeyService().make(groupId)
    assert(groupKey.getCreated.await().exists(_.isEqual(time)))

    val groupPairKey = GroupPairKeyService().make(userId, authCtx.userId)
    assert(groupPairKey.get.await().contains(groupId))

    val groupUsersKey = GroupUsersKeyService().make(groupId)
    assert(groupUsersKey.isMember(userId).await())
    assert(groupUsersKey.isMember(authCtx.userId).await())

    val userGroupsKeyA = UserGroupsKeyService().make(userId)
    assert(userGroupsKeyA.isMember(groupId).await())

    val userGroupsKeyB = UserGroupsKeyService().make(authCtx.userId)
    assert(userGroupsKeyB.isMember(groupId).await())

    unregister()
  }

  test("success - user exists") {
    val phoneKey = PhoneKeyService().make(phone)
    assert(phoneKey.setUserId(userId).await())

    val eventCount = 0
    val users = List(UserModel(userId, Option(label)))
    val groupId = randomMock.nextUuid()
    val group = GroupModel(groupId, users, Option(time), Option(time), eventCount)
    val groupService = mock[GroupService]
    (groupService.getGroup(_: UUID)(_: Pipeline, _: AuthContext)) expects
      (*, *, *) returning Future.successful(Right(Option(group)))
    val unregister = TestServices.register(groupService)
    val response = InviteResponseModel(userId, group)
    assert(service.innerExecute(model).await() == Right(response))

    val userUserLabelKey = UserUserLabelKeyService().make(authCtx.userId, userId)
    assert(userUserLabelKey.get.await().contains(label))

    val groupKey = GroupKeyService().make(groupId)
    assert(groupKey.getCreated.await().exists(_.isEqual(time)))

    val groupPairKey = GroupPairKeyService().make(userId, authCtx.userId)
    assert(groupPairKey.get.await().contains(groupId))

    val groupUsersKey = GroupUsersKeyService().make(groupId)
    assert(groupUsersKey.isMember(userId).await())
    assert(groupUsersKey.isMember(authCtx.userId).await())

    val userGroupsKeyA = UserGroupsKeyService().make(userId)
    assert(userGroupsKeyA.isMember(groupId).await())

    val userGroupsKeyB = UserGroupsKeyService().make(authCtx.userId)
    assert(userGroupsKeyB.isMember(groupId).await())

    unregister()
  }

  test("success - group exists") {
    val phoneKey = PhoneKeyService().make(phone)
    assert(phoneKey.setUserId(userId).await())

    val groupPairKey = GroupPairKeyService().make(userId, authCtx.userId)
    assert(groupPairKey.set(groupId).await())

    val eventCount = 0
    val users = List(UserModel(userId, Option(label)))
    val group = GroupModel(groupId, users, Option(time), Option(time), eventCount)
    val groupService = mock[GroupService]
    (groupService.getGroup(_: UUID)(_: Pipeline, _: AuthContext)) expects
      (*, *, *) returning Future.successful(Right(Option(group)))
    val unregister = TestServices.register(groupService)
    val response = InviteResponseModel(userId, group)
    assert(service.innerExecute(model).await() == Right(response))

    unregister()
  }

  test("user create failed") {
    val userKey = mock[UserKey]
    val phoneKey = mock[PhoneKey]
    userKey.create _ expects () returning Future.successful(false)
    val actual = command.userCreate(userId, userKey, phoneKey)
    assert(actual.await() == Errors.userCreateFailed.await())
  }

  test("phone set userId failed") {
    val key = mock[PhoneKey]
    key.setUserId _ expects * returning Future.successful(false)
    assert(command.phoneSetUserId(userId, key).await() == Errors.phoneSetUserIdFailed.await())
  }

  test("user user label set failed") {
    val key = mock[UserUserLabelKey]
    key.set _ expects * returning Future.successful(false)
    assert(command.userUserLabelSet(userId, key).await() == Errors.userUserLabelSetFailed.await())
  }

  test("group pair set failed") {
    val groupKey = mock[GroupKey]
    val pairKey = mock[GroupPairKey]
    groupKey.create _ expects () returning Future.successful(true)
    pairKey.set _ expects * returning Future.successful(false)
    val actual = command.groupPairSet(userId, groupId, groupKey, pairKey)
    assert(actual.await() == Errors.pairSetFailed.await())
  }

  test("group user add failed") {
    val key = mock[GroupUsersKey]
    key.add _ expects * returning Future.successful(0)
    assert(command.groupUsersAdd(userId, groupId, key).await() == Errors.groupUsersAddFailed.await())
  }

  test("invited user groups add failed") {
    val key = mock[UserGroupsKey]
    key.add _ expects * returning Future.successful(0L)
    assert(command.invitedUserGroupsAdd(userId, groupId, key).await() == Errors.userGroupsAddFailed.await())
  }

  test("inviting user groups add failed") {
    val key = mock[UserGroupsKey]
    key.add _ expects * returning Future.successful(0L)
    assert(command.invitingUserGroupsAdd(userId, groupId, key).await() == Errors.userGroupsAddFailed.await())
  }

  test("group events prepend returned error") {
    val groupService = mock[GroupService]
    (groupService.getGroup(_: UUID)(_: Pipeline, _: AuthContext)) expects
      (*, *, *) returning Future.successful(Left(error))
    val unregister = TestServices.register(groupService)
    assert(command.groupGet(userId, groupId).await() == Left(error))
    unregister()
  }

  test("group get returned error") {
    val groupEventsService = mock[GroupEventsService]
    (groupEventsService.setEvent(_: EventModel, _: Boolean)(_: Pipeline)) expects
      (*, *, *) returning Future.successful(Left(error))
    val unregister = TestServices.register(groupEventsService)
    assert(command.groupEventsPrepend(userId, groupId).await() == Left(error))
    unregister()
  }

  test("group get failed") {
    val groupService = mock[GroupService]
    (groupService.getGroup(_: UUID)(_: Pipeline, _: AuthContext)) expects
      (*, *, *) returning Future.successful(Right(None))
    val unregister = TestServices.register(groupService)
    assert(command.groupGet(userId, groupId).await() == Errors.groupGetFailed.await())
    unregister()
  }

}
