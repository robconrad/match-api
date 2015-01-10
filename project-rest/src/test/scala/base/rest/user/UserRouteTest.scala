/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/9/15 8:55 PM
 */

package base.rest.user

import base.common.random.RandomService
import base.common.service.ServicesBeforeAndAfterAll
import base.entity.api.ApiVersions
import base.entity.model.ModelImplicits
import base.rest.Endpoint._
import base.rest.route.{ RouteTest, RouteTestCompanion }
import org.scalatest.BeforeAndAfterEach

/**
 * Tests the UserRoute and route-level validation - not an exhaustive test of users overall,
 * more validation tests can be found in UserServiceImplTest
 * @author rconrad
 */
class UserRouteTest extends RouteTest(UserRouteTest)
    with UserRoute with ServicesBeforeAndAfterAll with ModelImplicits with BeforeAndAfterEach {

  val version = ApiVersions.latest

  //  private implicit val authContext = AuthContextDataFactory.emptyKeyAuth
  //
  //  private val authMock = new AuthServiceMock(authContext)
  //  private val userMock = new UserServiceMock()
  //
  //  private val userUuid = userMock.getNextUserUUID
  //  private val email = "email@foo.com"
  //  private val postUser = RegisterModel(email, "password")
  //
  //  private lazy val user = UserDataFactory(email, "password")
  //
  //  private lazy val userResponseJson = Serialization.write(
  //    UserModel(userUuid, email, active = true, now))
  //
  //  override def beforeAll() {
  //    super.beforeAll()
  //    Services.register(authMock)
  //    Services.register(userMock)
  //    Services.register(TimeServiceConstantMock)
  //  }
  //
  //  override def beforeEach() {
  //    super.beforeEach()
  //    authMock.setAuthContext(authContext)
  //  }
  //
  //  test("create user - good payload") {
  //    authMock.setAuthContext(authContext)
  //    Post(USERS, postUser) ~> addHeader(ApiKeyTypes.API.header, "foo") ~> sealedRoute ~> check {
  //      assert(status == StatusCodes.OK)
  //      assert(decompressed == userResponseJson, "good response returned")
  //      assertCorsHeaders(response)
  //    }
  //  }
  //
  //  test("create user - bad payload") {
  //    authMock.setAuthContext(authContext)
  //    Post(USERS, "{}") ~> addHeader(ApiKeyTypes.API.header, "foo") ~> sealedRoute ~> check {
  //      assert(status == StatusCodes.BadRequest)
  //      assertCorsHeaders(response)
  //    }
  //  }
  //
  //  test("get user me") {
  //    implicit val authContext = StandardUserAuthContext(user)
  //    authMock.setAuthContext(authContext)
  //    Get(USERS_ME) ~> addCredentials(BasicHttpCredentials("user", "pass")) ~> sealedRoute ~> check {
  //      assert(response.status == StatusCodes.OK, "status code")
  //      //assert(decompressed == Serialization.write(UserModel(authContext.userThrows)))
  //    }
  //  }

}

object UserRouteTest extends RouteTestCompanion {

  val CORS_ENDPOINTS = List[String](USERS, USERS + "/" + RandomService().uuid, USERS_ME, USERS_RESET)

}
