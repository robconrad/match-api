/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 5:09 PM
 */

package base.entity.group.impl

import java.util.UUID

import base.common.random.RandomService
import base.common.random.mock.RandomServiceMock
import base.common.service.{Services, TestServices}
import base.common.time.mock.TimeServiceConstantMock
import base.entity.auth.context.{ChannelContext, ChannelContextDataFactory}
import base.entity.command.impl.CommandServiceImplTest
import base.entity.error.ApiErrorService
import base.entity.event.model.EventModel
import base.entity.group.impl.AcceptInviteCommandServiceImpl.Errors
import base.entity.group.kv._
import base.entity.group.model.{AcceptInviteModel, AcceptInviteResponseModel, GroupModel}
import base.entity.group.{GroupEventsService, GroupListenerService, GroupService}
import base.entity.kv.Key._
import base.entity.question.QuestionService
import base.entity.user.kv._

import scala.concurrent.Future

/**
 * Responsible for testing the logic and side effects of the Impl
 * (i.e. validation, persistence, etc.)
 * @author rconrad
 */
// scalastyle:off null
class AcceptInviteCommandServiceImplTest extends CommandServiceImplTest {

  val service = new AcceptInviteCommandServiceImpl("joined!")

  private val groupId = RandomService().uuid

  private val error = ApiErrorService().badRequest("test")

  private val randomMock = new RandomServiceMock()

  private implicit val channelCtx = ChannelContextDataFactory.userAuth
  private implicit val model = AcceptInviteModel(groupId)

  override def beforeAll() {
    super.beforeAll()
    Services.register(TimeServiceConstantMock)
    Services.register(randomMock)
  }

  private def command(implicit input: AcceptInviteModel) = new service.AcceptInviteCommand(input)

  test("without perms") {
    assertPermException(channelCtx => {
      service.execute(model)(channelCtx)
    })
  }

  test("success") {
    val phone = "555-1234"
    val groupId = RandomService().uuid
    val eventModel = mock[EventModel]
    val groupModel = mock[GroupModel]
    val groupEventsService = mock[GroupEventsService]
    val groupListenerService = mock[GroupListenerService]
    val groupService = mock[GroupService]
    val questionService = mock[QuestionService]

    (groupEventsService.setEvent(_: EventModel, _: Boolean)(_: Pipeline)) expects
      (*, *, *) returning Future.successful(Right(eventModel))
    (groupListenerService.register(_: UUID, _: Set[UUID])(_: ChannelContext)) expects
      (*, *, *) returning Future.successful(Unit)
    (groupService.getGroup(_: UUID, _: UUID)(_: Pipeline, _: ChannelContext)) expects
      (*, *, *, *) returning Future.successful(Right(Option(groupModel)))
    (questionService.getQuestions(_: UUID, _: UUID)(_: Pipeline, _: ChannelContext)) expects
      (*, *, *, *) returning Future.successful(Right(List()))
    (groupEventsService.getEvents(_: UUID)(_: Pipeline)) expects
      (*, *) returning Future.successful(Right(List()))

    val unregister = TestServices.register(groupService, groupEventsService, groupListenerService, questionService)
    val response = AcceptInviteResponseModel(groupModel, List(), List())

    val userGroupsInvitedKey = UserGroupsInvitedKeyService().make(authCtx.userId)
    assert(userGroupsInvitedKey.add(groupId).await() == 1L)

    val groupPhonesInvitedKey = GroupPhonesInvitedKeyService().make(groupId)
    assert(groupPhonesInvitedKey.add(phone).await() == 1L)

    val userKey = UserKeyService().make(authCtx.userId)
    assert(userKey.setPhoneAttributes(UserPhoneAttributes(phone, "", verified = true)).await())

    val actual = service.innerExecute(AcceptInviteModel(groupId)).await()
    val expected = Right(response)

    debug(actual.toString)
    debug(expected.toString)

    assert(actual == expected)

    assert(!userGroupsInvitedKey.isMember(groupId).await())

    val groupUsersKey = GroupUsersKeyService().make(groupId)
    assert(groupUsersKey.isMember(authCtx.userId).await())

    val userGroupsKey = UserGroupsKeyService().make(authCtx.userId)
    assert(userGroupsKey.isMember(groupId).await())

    assert(!groupPhonesInvitedKey.isMember(phone).await())

    unregister()
  }

  test("user groups invited remove failed") {
    val key = mock[UserGroupsInvitedKey]
    key.remove _ expects * returning Future.successful(0)
    assert(command.userGroupsInvitedRemove(key).await() == Errors.userGroupsInvitedRemoveFailed.await())
  }

  test("user get phone not verified") {
    val key = mock[UserKey]
    val attributes = UserPhoneAttributes("", "", verified = false)
    key.getPhoneAttributes _ expects () returning Future.successful(Option(attributes))
    assert(command.userGetPhone(key).await() == Errors.userPhoneNotVerified.await())
  }

  test("group users add failed") {
    val key = mock[GroupUsersKey]
    key.add _ expects * returning Future.successful(0)
    assert(command.groupUsersAdd(key).await() == Errors.groupUsersAddFailed.await())
  }

  test("user groups add failed") {
    val key = mock[UserGroupsKey]
    key.add _ expects * returning Future.successful(0L)
    assert(command.userGroupsAdd(key).await() == Errors.userGroupsAddFailed.await())
  }

  test("group events prepend returned error") {
    val groupEventsService = mock[GroupEventsService]
    (groupEventsService.setEvent(_: EventModel, _: Boolean)(_: Pipeline)) expects
      (*, *, *) returning Future.successful(Left(error))
    val unregister = TestServices.register(groupEventsService)
    assert(command.groupEventsPrepend().await() == Left(error))
    unregister()
  }

  test("group get returned error") {
    val groupService = mock[GroupService]
    (groupService.getGroup(_: UUID, _: UUID)(_: Pipeline, _: ChannelContext)) expects
      (*, *, *, *) returning Future.successful(Left(error))
    val unregister = TestServices.register(groupService)
    assert(command.groupGet().await() == Left(error))
    unregister()
  }

  test("group get failed") {
    val groupService = mock[GroupService]
    (groupService.getGroup(_: UUID, _: UUID)(_: Pipeline, _: ChannelContext)) expects
      (*, *, *, *) returning Future.successful(Right(None))
    val unregister = TestServices.register(groupService)
    assert(command.groupGet().await() == Errors.groupGetFailed.await())
    unregister()
  }

  test("questions get returned error") {
    val groupModel = mock[GroupModel]
    val questionService = mock[QuestionService]
    (questionService.getQuestions(_: UUID, _: UUID)(_: Pipeline, _: ChannelContext)) expects
      (*, *, *, *) returning Future.successful(Left(error))
    val unregister = TestServices.register(questionService)
    assert(command.questionsGet(groupModel).await() == Left(error))
    unregister()
  }

  test("events get returned error") {
    val groupModel = mock[GroupModel]
    val groupEventsService = mock[GroupEventsService]
    (groupEventsService.getEvents(_: UUID)(_: Pipeline)) expects
      (*, *) returning Future.successful(Left(error))
    val unregister = TestServices.register(groupEventsService)
    assert(command.eventsGet(groupModel, List()).await() == Left(error))
    unregister()
  }

}
