/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 10:56 AM
 */

package base.entity.group.impl

import java.util.UUID

import base.common.random.RandomService
import base.common.random.mock.RandomServiceMock
import base.common.service.{ Services, TestServices }
import base.common.time.mock.TimeServiceConstantMock
import base.entity.auth.context.{ ChannelContext, ChannelContextDataFactory }
import base.entity.command.impl.CommandServiceImplTest
import base.entity.error.ApiErrorService
import base.entity.event.model.EventModel
import base.entity.group.impl.InviteCommandServiceImpl.Errors
import base.entity.group.kv._
import base.entity.group.model.impl.GroupModelImpl
import base.entity.group.model.{ GroupModel, InviteModel, InviteResponseModel }
import base.entity.group.{ GroupEventsService, GroupListenerService, GroupService }
import base.entity.kv.Key._
import base.entity.question.QuestionService
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

  private val error = ApiErrorService().badRequest("test")

  private val randomMock = new RandomServiceMock()

  private implicit val channelCtx = ChannelContextDataFactory.userAuth
  private implicit val model = InviteModel(phone, label)

  override def beforeAll() {
    super.beforeAll()
    Services.register(TimeServiceConstantMock)
    Services.register(randomMock)
  }

  private def command(implicit input: InviteModel) = new service.InviteCommand(input)

  private def testSuccess(userExists: Boolean) = {
    val eventCount = 0
    val userId = randomMock.nextUuid()
    val users = List(UserModel(userId, Option(label)))
    val eventModel = mock[EventModel]
    val groupId = randomMock.nextUuid()
    val group = GroupModelImpl(groupId, users, Option(time), Option(time), eventCount)
    val groupEventsService = mock[GroupEventsService]
    val groupListenerService = mock[GroupListenerService]
    val groupService = mock[GroupService]
    val questionService = mock[QuestionService]

    (groupEventsService.setEvent(_: EventModel, _: Boolean)(_: Pipeline)) expects
      (*, *, *) returning Future.successful(Right(eventModel))
    (groupListenerService.register(_: UUID, _: Set[UUID])(_: ChannelContext)) expects
      (*, *, *) returning Future.successful(Unit)
    (groupService.getGroup(_: UUID, _: UUID)(_: Pipeline, _: ChannelContext)) expects
      (*, *, *, *) returning Future.successful(Right(Option(group)))
    (questionService.getQuestions(_: UUID)(_: Pipeline, _: ChannelContext)) expects
      (*, *, *) returning Future.successful(Right(List()))
    (groupEventsService.getEvents(_: UUID)(_: Pipeline)) expects
      (*, *) returning Future.successful(Right(List()))

    val unregister = TestServices.register(groupService, groupEventsService, groupListenerService, questionService)
    val response = InviteResponseModel(group, List(), List())

    if (userExists) {
      val phoneKey = PhoneKeyService().make(phone)
      phoneKey.set(userId)
    }

    val actual = service.innerExecute(model).await()
    val expected = Right(response)

    debug(actual.toString)
    debug(expected.toString)

    assert(actual == expected)

    val userPhonesInvitedKey = UserPhonesInvitedKeyService().make(authCtx.userId)
    assert(userPhonesInvitedKey.isMember(phone).await())

    val userPhoneLabelKey = UserPhoneLabelKeyService().make(UserPhone(authCtx.userId, phone))
    assert(userPhoneLabelKey.get.await().contains(label))

    val groupKey = GroupKeyService().make(groupId)
    assert(groupKey.getCreated.await().exists(_.isEqual(time)))

    userExists match {
      case true =>
        val userGroupsInvitedKey = UserGroupsInvitedKeyService().make(userId)
        assert(userGroupsInvitedKey.isMember(groupId).await())
      case false =>
        val phoneGroupsInvitedKey = PhoneGroupsInvitedKeyService().make(phone)
        assert(phoneGroupsInvitedKey.isMember(groupId).await())
    }

    val groupUsersKey = GroupUsersKeyService().make(groupId)
    assert(groupUsersKey.isMember(authCtx.userId).await())

    val userGroups = UserGroupsKeyService().make(authCtx.userId)
    assert(userGroups.isMember(groupId).await())

    unregister()
  }

  test("without perms") {
    assertPermException(channelCtx => {
      service.execute(model)(channelCtx)
    })
  }

  test("success - new user") {
    testSuccess(userExists = false)
  }

  test("success - user exists") {
    testSuccess(userExists = true)
  }

  test("user already invited this phone") {
    val key = mock[UserPhonesInvitedKey]
    key.add _ expects * returning Future.successful(0L)
    assert(command.userPhonesInvitedAdd(key).await() == Errors.alreadyInvited.await())
  }

  test("user phones invited add failed") {
    val key = mock[UserPhonesInvitedKey]
    key.add _ expects * returning Future.successful(-1L)
    assert(command.userPhonesInvitedAdd(key).await() == Errors.userInvitedPhonesSetFailed.await())
  }

  test("user phone label set failed") {
    val key = mock[UserPhoneLabelKey]
    key.set _ expects * returning Future.successful(false)
    assert(command.userPhoneLabelSet(key).await() == Errors.userPhoneLabelSetFailed.await())
  }

  test("group create failed") {
    val key = mock[GroupKey]
    key.create _ expects () returning Future.successful(false)
    assert(command.groupCreate(groupId, key).await() == Errors.groupCreateFailed.await())
  }

  test("phone groups invited add failed") {
    val key = mock[PhoneGroupsInvitedKey]
    key.add _ expects * returning Future.successful(0L)
    assert(command.phoneGroupsInvitedAdd(groupId, key).await() == Errors.phoneGroupsInvitedAddFailed.await())
  }

  test("user groups invited add failed") {
    val key = mock[UserGroupsInvitedKey]
    key.add _ expects * returning Future.successful(0L)
    assert(command.userGroupsInvitedAdd(groupId, key).await() == Errors.userGroupsInvitedAddFailed.await())
  }

  test("group users add failed") {
    val key = mock[GroupUsersKey]
    key.add _ expects * returning Future.successful(0)
    assert(command.groupUsersAdd(groupId, key).await() == Errors.groupUsersAddFailed.await())
  }

  test("user groups add failed") {
    val key = mock[UserGroupsKey]
    key.add _ expects * returning Future.successful(0L)
    assert(command.userGroupsAdd(groupId, key).await() == Errors.userGroupsAddFailed.await())
  }

  test("group events prepend returned error") {
    val groupEventsService = mock[GroupEventsService]
    (groupEventsService.setEvent(_: EventModel, _: Boolean)(_: Pipeline)) expects
      (*, *, *) returning Future.successful(Left(error))
    val unregister = TestServices.register(groupEventsService)
    assert(command.groupEventsPrepend(groupId).await() == Left(error))
    unregister()
  }

  test("group get returned error") {
    val groupService = mock[GroupService]
    (groupService.getGroup(_: UUID, _: UUID)(_: Pipeline, _: ChannelContext)) expects
      (*, *, *, *) returning Future.successful(Left(error))
    val unregister = TestServices.register(groupService)
    assert(command.groupGet(groupId).await() == Left(error))
    unregister()
  }

  test("group get failed") {
    val groupService = mock[GroupService]
    (groupService.getGroup(_: UUID, _: UUID)(_: Pipeline, _: ChannelContext)) expects
      (*, *, *, *) returning Future.successful(Right(None))
    val unregister = TestServices.register(groupService)
    assert(command.groupGet(groupId).await() == Errors.groupGetFailed.await())
    unregister()
  }

  test("questions get returned error") {
    val groupModel = mock[GroupModel]
    val questionService = mock[QuestionService]
    (questionService.getQuestions(_: UUID)(_: Pipeline, _: ChannelContext)) expects
      (*, *, *) returning Future.successful(Left(error))
    val unregister = TestServices.register(questionService)
    assert(command.questionsGet(groupId, groupModel).await() == Left(error))
    unregister()
  }

  test("events get returned error") {
    val groupModel = mock[GroupModel]
    val groupEventsService = mock[GroupEventsService]
    (groupEventsService.getEvents(_: UUID)(_: Pipeline)) expects
      (*, *) returning Future.successful(Left(error))
    val unregister = TestServices.register(groupEventsService)
    assert(command.eventsGet(groupId, groupModel, List()).await() == Left(error))
    unregister()
  }

}
