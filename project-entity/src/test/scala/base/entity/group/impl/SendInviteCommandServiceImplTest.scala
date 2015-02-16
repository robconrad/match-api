/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 5:52 PM
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
import base.entity.group.impl.SendInviteCommandServiceImpl.Errors
import base.entity.group.kv._
import base.entity.group.model.impl.GroupModelImpl
import base.entity.group.model.{ GroupModel, SendInviteModel, SendInviteResponseModel }
import base.entity.group.{ GroupEventsService, GroupListenerService, GroupService }
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
class SendInviteCommandServiceImplTest extends CommandServiceImplTest {

  val service = new SendInviteCommandServiceImpl("welcome!")

  private val phone = "555-1234"
  private val label = "Bob"
  private val time = TimeServiceConstantMock.now

  private val groupId = RandomService().uuid

  private val error = ApiErrorService().badRequest("test")

  private val randomMock = new RandomServiceMock()

  private implicit val channelCtx = ChannelContextDataFactory.userAuth
  private implicit val model = SendInviteModel(phone, label)

  override def beforeAll() {
    super.beforeAll()
    Services.register(TimeServiceConstantMock)
    Services.register(randomMock)
  }

  private def command(implicit input: SendInviteModel) = new service.InviteCommand(input)

  private def testSuccess(userExists: Boolean) = {
    val eventCount = 0
    val userId = randomMock.nextUuid()
    val users = List(UserModel(userId, None, Option(label)))
    val eventModel = mock[EventModel]
    val groupId = randomMock.nextUuid()
    val group = GroupModelImpl(groupId, users, List(), Option(time), Option(time), eventCount)
    val groupEventsService = mock[GroupEventsService]
    val groupListenerService = mock[GroupListenerService]
    val groupService = mock[GroupService]
    val questionService = mock[QuestionService]

    groupEventsService.setEvent _ expects (*, *) returning Future.successful(Right(eventModel))
    (groupListenerService.register(_: UUID, _: Set[UUID])(_: ChannelContext)) expects
      (*, *, *) returning Future.successful(Unit)
    (groupService.getGroup(_: UUID, _: UUID)(_: ChannelContext)) expects
      (*, *, *) returning Future.successful(Right(Option(group)))
    (questionService.getQuestions(_: UUID, _: UUID)(_: ChannelContext)) expects
      (*, *, *) returning Future.successful(Right(List()))
    groupEventsService.getEvents _ expects * returning Future.successful(Right(List()))


    val unregister = TestServices.register(groupService, groupEventsService, groupListenerService, questionService)
    val response = SendInviteResponseModel(group, List(), List())

    if (userExists) {
      val phoneKey = make[PhoneKey](phone)
      phoneKey.set(userId)
    }

    val actual = service.innerExecute(model).await()
    val expected = Right(response)

    debug(actual.toString)
    debug(expected.toString)

    assert(actual == expected)

    val userPhonesInvitedKey = make[UserPhonesInvitedKey](authCtx.userId)
    assert(userPhonesInvitedKey.isMember(phone).await())

    val userPhoneLabelKey = make[UserPhoneLabelKey](UserPhone(authCtx.userId, phone))
    assert(userPhoneLabelKey.get.await().contains(label))

    val groupKey = make[GroupKey](groupId)
    assert(groupKey.getCreated.await().exists(_.isEqual(time)))

    userExists match {
      case true =>
        val userGroupsInvitedKey = make[UserGroupsInvitedKey](userId)
        assert(userGroupsInvitedKey.isMember(groupId).await())
      case false =>
        val phoneGroupsInvitedKey = make[PhoneGroupsInvitedKey](phone)
        assert(phoneGroupsInvitedKey.isMember(groupId).await())
    }

    val groupUsersKey = make[GroupUsersKey](groupId)
    assert(groupUsersKey.isMember(authCtx.userId).await())

    val userGroups = make[UserGroupsKey](authCtx.userId)
    assert(userGroups.isMember(groupId).await())

    val groupPhonesInvitedKey = make[GroupPhonesInvitedKey](groupId)
    assert(groupPhonesInvitedKey.isMember(phone).await())

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
    key.set _ expects (*, *, *) returning Future.successful(false)
    assert(command.userPhoneLabelSet(key).await() == Errors.userPhoneLabelSetFailed.await())
  }

  test("user invited self") {
    val key = mock[UserPhonesInvitedKey]
    key.add _ expects * returning Future.successful(0L)
    assert(command.userPhonesInvitedAdd(key).await() == Errors.alreadyInvited.await())
  }

  test("phone groups invited add failed") {
    val key = mock[PhoneKey]
    key.get _ expects () returning Future.successful(Option(authCtx.userId))
    assert(command.phoneGetUserId(groupId, key).await() == Errors.selfInvited.await())
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

  test("group phones invited add failed") {
    val key = mock[GroupPhonesInvitedKey]
    key.add _ expects * returning Future.successful(0L)
    assert(command.groupPhonesInvitedAdd(groupId, key).await() == Errors.groupPhonesInvitedAddFailed.await())
  }

  test("group events prepend returned error") {
    val groupEventsService = mock[GroupEventsService]
    groupEventsService.setEvent _ expects (*, *) returning Future.successful(Left(error))
    val unregister = TestServices.register(groupEventsService)
    assert(command.groupEventsPrepend(groupId).await() == Left(error))
    unregister()
  }

  test("group get returned error") {
    val groupService = mock[GroupService]
    (groupService.getGroup(_: UUID, _: UUID)(_: ChannelContext)) expects
      (*, *, *) returning Future.successful(Left(error))
    val unregister = TestServices.register(groupService)
    assert(command.groupGet(groupId).await() == Left(error))
    unregister()
  }

  test("group get failed") {
    val groupService = mock[GroupService]
    (groupService.getGroup(_: UUID, _: UUID)(_: ChannelContext)) expects
      (*, *, *) returning Future.successful(Right(None))
    val unregister = TestServices.register(groupService)
    assert(command.groupGet(groupId).await() == Errors.groupGetFailed.await())
    unregister()
  }

  test("questions get returned error") {
    val groupModel = mock[GroupModel]
    val questionService = mock[QuestionService]
    (questionService.getQuestions(_: UUID, _: UUID)(_: ChannelContext)) expects
      (*, *, *) returning Future.successful(Left(error))
    val unregister = TestServices.register(questionService)
    assert(command.questionsGet(groupId, groupModel).await() == Left(error))
    unregister()
  }

  test("events get returned error") {
    val groupModel = mock[GroupModel]
    val groupEventsService = mock[GroupEventsService]
    groupEventsService.getEvents _ expects * returning Future.successful(Left(error))
    val unregister = TestServices.register(groupEventsService)
    assert(command.eventsGet(groupId, groupModel, List()).await() == Left(error))
    unregister()
  }

}
