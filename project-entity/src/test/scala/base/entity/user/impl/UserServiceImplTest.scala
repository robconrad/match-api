/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:42 PM
 */

package base.entity.user.impl

import java.util.UUID

import base.common.random.RandomService
import base.common.service.TestServices
import base.entity.auth.context.{ AuthContext, AuthContextDataFactory }
import base.entity.error.ApiError
import base.entity.group.mock.GroupServiceMock
import base.entity.group.model.GroupModel
import base.entity.kv.Key._
import base.entity.kv.KvTest
import base.entity.kv.mock.{ SetKeyMock, StringKeyMock }
import base.entity.service.EntityServiceTest
import base.entity.user.impl.UserServiceImpl.Errors
import base.entity.user.kv.impl.{ UserUserLabelKeyImpl, UserUserLabelKeyServiceImpl }
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
    val key = new StringKeyMock(getResult = Future.successful(Option(label)))
    val model = UserModel(userId, Option(label))
    assert(service.getUser(userId, key).await() == Right(model))
  }

  test("getUsers") {
    val userIds = List(userId1, userId2)
    val model1 = UserModel(userId1, Option(label))
    val model2 = UserModel(userId2, None)
    val models = List(model1, model2)
    val unregister = TestServices.register(new UserUserLabelKeyServiceImpl {
      override def make(id: Id)(implicit p: Pipeline) = new UserUserLabelKeyImpl(id.toString, this)(p) {
        override def get = {
          id.id == authCtx.userId + "-" + userId2 match {
            case false => Future.successful(Option(label))
            case true  => Future.successful(None)
          }
        }
      }
    })
    assert(service.getUsers(userIds).await() == Right(models))
    unregister()
  }

  test("getGroups - success") {
    val groups = Set(groupId1, groupId2).map(_.toString)
    val group1 = GroupModel(groupId1, List(), None, None, eventCount = 0)
    val group2 = group1.copy(id = groupId2)
    val key = new SetKeyMock(membersResult = Future.successful(groups))
    val groupMock = new GroupServiceMock() {
      override def getGroup(groupId: UUID)(implicit p: Pipeline, authCtx: AuthContext) = groupId == groupId1 match {
        case true  => Future.successful(Right(Option(group1)))
        case false => Future.successful(Right(Option(group2)))
      }
    }
    val unregister = TestServices.register(groupMock)
    assert(service.getGroups(userId, key).await() == Right(List(group1, group2)))
    unregister()
  }

  test("getGroups - GroupService ApiError") {
    val groups = Set(groupId1, groupId2).map(_.toString)
    val key = new SetKeyMock(membersResult = Future.successful(groups))
    val error = ApiError("whatever")
    val groupMock = new GroupServiceMock(getGroupResult = Future.successful(Left(error)))
    val unregister = TestServices.register(groupMock)
    assert(service.getGroups(userId, key).await() == Left(error))
    unregister()
  }

  test("getGroups - not all groups returned") {
    val groups = Set(groupId1, groupId2).map(_.toString)
    val group = GroupModel(groupId1, List(), None, None, eventCount = 0)
    val key = new SetKeyMock(membersResult = Future.successful(groups))
    val groupMock = new GroupServiceMock(getGroupResult = Future.successful(Right(Option(group))))
    val unregister = TestServices.register(groupMock)
    assert(service.getGroups(userId, key).await() == Errors.notAllGroupsReturned)
    unregister()
  }

}
