/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:12 PM
 */

package base.entity.group.impl

import base.common.random.RandomService
import base.common.random.mock.RandomServiceMock
import base.common.service.Services
import base.common.time.mock.TimeServiceConstantMock
import base.entity.auth.context.ChannelContextDataFactory
import base.entity.command.impl.CommandServiceImplTest
import base.entity.group.impl.DeclineInviteCommandServiceImpl.Errors
import base.entity.group.model._
import base.entity.user.kv._

import scala.concurrent.Future

/**
 * Responsible for testing the logic and side effects of the Impl
 * (i.e. validation, persistence, etc.)
 * @author rconrad
 */
// scalastyle:off null
class DeclineInviteCommandServiceImplTest extends CommandServiceImplTest[DeclineInviteModel] {

  val service = new DeclineInviteCommandServiceImpl()

  private val groupId = RandomService().uuid

  private val randomMock = new RandomServiceMock()

  private implicit val channelCtx = ChannelContextDataFactory.userAuth()
  implicit val model = DeclineInviteModel(groupId)

  override def beforeAll() {
    super.beforeAll()
    Services.register(TimeServiceConstantMock)
    Services.register(randomMock)
  }

  private def command(implicit input: DeclineInviteModel) = new service.DeclineInviteCommand(input)

  test("success") {
    val groupId = RandomService().uuid

    val response = DeclineInviteResponseModel(groupId)

    val userGroupsInvitedKey = make[UserGroupsInvitedKey](authCtx.userId)
    assert(userGroupsInvitedKey.add(groupId).await() == 1L)

    val actual = service.innerExecute(DeclineInviteModel(groupId)).await()
    val expected = Right(response)
    assert(actual == expected)

    assert(!userGroupsInvitedKey.isMember(groupId).await())
  }

  test("user groups invited remove failed") {
    val key = mock[UserGroupsInvitedKey]
    key.rem _ expects * returning Future.successful(0)
    assert(command.userGroupsInvitedRemove(key).await() == Errors.userGroupsInvitedRemoveFailed.await())
  }

}
