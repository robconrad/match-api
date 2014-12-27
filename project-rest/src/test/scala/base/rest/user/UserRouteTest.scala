/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/25/14 11:17 AM
 */

package base.rest.user

import base.common.random.RandomService
import base.common.service.{ Services, ServicesBeforeAndAfterAll }
import base.common.time.DateTimeHelper
import base.common.time.mock.TimeServiceConstantMock
import base.entity.apiKey.ApiKeyTypes
import base.entity.auth.context.{ AuthContextDataFactory, StandardUserAuthContext }
import base.entity.auth.mock.AuthServiceMock
import base.entity.model.ModelImplicits
import base.entity.user.UserDataFactory
import base.entity.user.mock.UserServiceMock
import base.entity.user.model.{ PostUserRequest, User }
import base.rest.Endpoint._
import base.rest.Versions
import base.rest.route.{ RouteTest, RouteTestCompanion }
import org.json4s.native.Serialization
import org.scalatest.BeforeAndAfterEach
import spray.http.{ BasicHttpCredentials, StatusCodes }

/**
 * Tests the UserRoute and route-level validation - not an exhaustive test of users overall,
 * more validation tests can be found in UserServiceImplTest
 * @author rconrad
 */
class UserRouteTest extends RouteTest(UserRouteTest)
    with UserRoute with ServicesBeforeAndAfterAll with DateTimeHelper with ModelImplicits with BeforeAndAfterEach {

  val version = Versions.latest

  private implicit val authContext = AuthContextDataFactory.emptyKeyAuth

  private val authMock = new AuthServiceMock(authContext)
  private val userMock = new UserServiceMock()

  private val userUuid = userMock.getNextUserUUID
  private val email = "email@foo.com"
  private val postUser = PostUserRequest(email, "password")

  private lazy val user = UserDataFactory(email, "password")

  private lazy val userResponseJson = Serialization.write(
    User(userUuid, email, active = true, now))

  override def beforeAll() {
    super.beforeAll()
    Services.register(authMock)
    Services.register(userMock)
    Services.register(TimeServiceConstantMock)
  }

  override def beforeEach() {
    super.beforeEach()
    authMock.setAuthContext(authContext)
  }

  test("create user - good payload") {
    authMock.setAuthContext(authContext)
    Post(USERS, postUser) ~> addHeader(ApiKeyTypes.API.header, "foo") ~> sealedRoute ~> check {
      assert(status == StatusCodes.OK)
      assert(decompressed == userResponseJson, "good response returned")
      assertCorsHeaders(response)
    }
  }

  test("create user - bad payload") {
    authMock.setAuthContext(authContext)
    Post(USERS, "{}") ~> addHeader(ApiKeyTypes.API.header, "foo") ~> sealedRoute ~> check {
      assert(status == StatusCodes.BadRequest)
      assertCorsHeaders(response)
    }
  }

  test("get user me") {
    implicit val authContext = StandardUserAuthContext(user)
    authMock.setAuthContext(authContext)
    Get(USERS_ME) ~> addCredentials(BasicHttpCredentials("user", "pass")) ~> sealedRoute ~> check {
      assert(response.status == StatusCodes.OK, "status code")
      assert(decompressed == Serialization.write(User(authContext.userThrows)))
    }
  }

}

object UserRouteTest extends RouteTestCompanion {

  val CORS_ENDPOINTS = List[String](USERS, USERS + "/" + RandomService().uuid, USERS_ME, USERS_RESET)

}
