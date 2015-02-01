/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 3:48 PM
 */

package base.entity.user.impl

import java.util.UUID

import base.common.random.RandomService
import base.common.random.mock.RandomServiceMock
import base.common.service.{ Services, TestServices }
import base.common.time.mock.TimeServiceConstantMock
import base.entity.auth.context.{ ChannelContext, ChannelContextDataFactory }
import base.entity.command.impl.CommandServiceImplTest
import base.entity.error.model.ApiError
import base.entity.kv.Key.Pipeline
import base.entity.sms.mock.SmsServiceMock
import base.entity.user.UserService
import base.entity.user.impl.VerifyPhoneCommandServiceImpl.Errors
import base.entity.user.kv._
import base.entity.user.model.{ VerifyPhoneModel, VerifyPhoneResponseModel }

import scala.concurrent.Future

/**
 * Responsible for testing the logic and side effects of the Impl
 * (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class VerifyPhoneCommandServiceImplTest extends CommandServiceImplTest {

  val codeLength = 6

  val service = new VerifyPhoneCommandServiceImpl(codeLength, "body %s")

  private val code = "Some Code"
  private val phone = "555-1234"

  private val randomMock = new RandomServiceMock()

  private implicit val channelCtx = ChannelContextDataFactory.userAuth
  private implicit val model = VerifyPhoneModel(phone, code)

  override def beforeAll() {
    super.beforeAll()
    Services.register(new SmsServiceMock(result = true))
    Services.register(TimeServiceConstantMock)
    Services.register(randomMock)
  }

  private def command(implicit input: VerifyPhoneModel) = new service.VerifyCommand(input)

  test("without perms") {
    assertPermException(channelCtx => {
      service.execute(model)(channelCtx)
    })
  }

  test("success") {
    val groupId1 = RandomService().uuid
    val groupId2 = RandomService().uuid
    val userKey = UserKeyService().make(authCtx.userId)
    val phoneKey = PhoneKeyService().make(phone)
    val phoneGroupsInvitedKey = PhoneGroupsInvitedKeyService().make(phone)
    val userGroupsInvitedKey = UserGroupsInvitedKeyService().make(authCtx.userId)
    val userService = mock[UserService]
    val unregister = TestServices.register(userService)

    (userService.getPendingGroups(_: UUID)(_: Pipeline, _: ChannelContext)) expects
      (*, *, *) returning Future.successful(Right(List()))

    assert(phoneKey.get.await() == None)

    assert(userKey.setPhoneAttributes(UserPhoneAttributes(phone, code, verified = false)).await())

    assert(phoneGroupsInvitedKey.add(groupId1).await() == 1)
    assert(userGroupsInvitedKey.add(groupId2).await() == 1)

    assert(service.innerExecute(model).await() == Right(VerifyPhoneResponseModel(phone, List())))

    val phoneAttributes = userKey.getPhoneAttributes.await()
    assert(phoneAttributes.exists(_.phone == phone))
    assert(phoneAttributes.exists(_.code == code))
    assert(phoneAttributes.exists(_.verified))

    assert(phoneKey.get.await().contains(authCtx.userId))

    assert(phoneGroupsInvitedKey.members().await().size == 0)
    assert(userGroupsInvitedKey.members().await().size == 2)

    unregister()
  }

  test("failed to get phone attributes") {
    val key = mock[UserKey]
    key.getPhoneAttributes _ expects () returning Future.successful(None)
    assert(command.userGetPhoneAttributes(key).await() == Errors.codeMissing.await())
  }

  test("failed to validate phone verification code") {
    val key = mock[UserKey]
    val code = model.code + "munge"
    assert(command.userVerifyPhoneCode(key, code).await() == Errors.codeValidation.await())
  }

  test("failed to set user phone verified") {
    val key = mock[UserKey]
    key.setPhoneVerified _ expects * returning Future.successful(false)
    assert(command.userSetPhoneVerified(key).await() == Errors.userSetPhoneVerifiedFailed.await())
  }

  test("failed to set phone user id") {
    val key = mock[PhoneKey]
    key.set _ expects * returning Future.successful(false)
    assert(command.phoneSetUserId(key).await() == Errors.phoneSetUserIdFailed.await())
  }

  test("get invitesIn returned error") {
    val apiError = mock[ApiError]
    val service = mock[UserService]
    (service.getPendingGroups(_: UUID)(_: Pipeline, _: ChannelContext)) expects
      (*, *, *) returning Future.successful(Left(apiError))
    assert(command.userGetInvitesIn(service).await() == Left(apiError))
  }

  test("send verify sms") {
    assert(service.sendVerifySms(phone, code).await())
  }

  test("make verify code") {
    val intMin = 100000
    val intMax = 1000000
    val randomMock = new RandomServiceMock(intMin = intMin, intMax = intMax)
    val unregister = TestServices.register(randomMock)
    val nextInt = randomMock.nextInt()
    val code = service.makeVerifyCode()
    assert(code == nextInt.toString)
    unregister()
  }

  test("validate verify codes") {
    val code1 = "abc "
    val code2 = " ABc"
    val code3 = "ABcd"
    assert(service.validateVerifyCodes(code1, code2))
    assert(!service.validateVerifyCodes(code1, code3))
  }

}
