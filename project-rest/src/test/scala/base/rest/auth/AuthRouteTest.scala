/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 9:45 PM
 */

package base.rest.auth

import base.common.service.{ Services, ServicesBeforeAndAfterAll }
import base.entity.api.ApiVersions
import base.entity.apiKey.ApiKeyTypes
import base.entity.auth.context.AuthContextDataFactory
import base.entity.auth.mock.AuthServiceMock
import base.rest.Endpoint._
import base.rest.route.{ RouteTest, RouteTestCompanion }
import spray.http.{ BasicHttpCredentials, StatusCodes }

/**
 * Tests Auth handling in isolation with a synthetic route
 * @author rconrad
 */
class AuthRouteTest extends RouteTest(AuthRouteTest) with AuthRoute with ServicesBeforeAndAfterAll {

  val version = ApiVersions.latest

  private val authMock = new AuthServiceMock()
  private val keyAuthCtx = AuthContextDataFactory.emptyKeyAuth
  private val userAuthCtx = AuthContextDataFactory.emptyUserAuth

  /**
   * Set up the database, add the good API keys
   */
  override def beforeAll() {
    super.beforeAll()
    Services.register(authMock)
  }

  /**
   * Test that logging in with a secret key succeeds
   */
  test(s"key - good auth (secret)") {
    authMock.setAuthContext(keyAuthCtx)
    Get(AUTH) ~> addHeader(ApiKeyTypes.API.header, "foo") ~> sealedRoute ~> check {
      assert(status == StatusCodes.OK)
    }
  }

  /**
   * Test that logging in with a publishable key succeeds
   */
  test(s"key - good auth (publishable)") {
    authMock.setAuthContext(keyAuthCtx)
    Get(AUTH) ~> addHeader(ApiKeyTypes.API.header, "foo") ~> sealedRoute ~> check {
      assert(status == StatusCodes.OK)
    }
  }

  /**
   * Test that logging in with creds that cause AuthService to return a context succeeds
   */
  test(s"user - good auth") {
    authMock.setAuthContext(userAuthCtx)
    Get(AUTH) ~> addCredentials(BasicHttpCredentials("user", "pass")) ~> sealedRoute ~> check {
      assert(status == StatusCodes.OK)
    }
  }

  /**
   * Test that not supplying any auth gets you the boot
   */
  test(s"bad auth (missing)") {
    authMock.setAuthContext(None)
    Get(AUTH) ~> sealedRoute ~> check {
      assert(status == StatusCodes.Unauthorized)
    }
  }

}

object AuthRouteTest extends RouteTestCompanion {

  val CORS_ENDPOINTS = List[String](AUTH)

}
