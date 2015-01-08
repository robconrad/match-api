/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/7/15 10:34 PM
 */

package base.rest.route

import base.rest.test.RestBaseSuite
import spray.testkit.ScalatestRouteTest

/**
 * Tests logic in the base RestRoute, primarily authentication header processing
 * @author rconrad
 */
class RestRouteTest extends RestBaseSuite with ScalatestRouteTest with RestRoute {

  val endpoints = List()

  def actorRefFactory = system
  implicit val ec = dispatcher

  //  private val perm = Perms.API_KEY_READ
  //
  //  private val userAuthCreds = Authorization(BasicHttpCredentials("Bob", ""))
  //  private val keyAuthHeader = addHeader(ApiKeyTypes.API.header, "foo")
  //
  //  private val keyWithGodPerms = AuthContextDataFactory.emptyKeyAuth
  //  private val userWithGodPerms = AuthContextDataFactory.emptyUserAuth
  //  private val userWithPublicPerms = new UserAuthContext {
  //    val user = None
  //    val provider = None
  //    val merchant = None
  //    val site = None
  //    val perms = PermSetGroups.public
  //    val authRole = AuthRoles.PUBLIC
  //  }
  //
  //  private val authMock = new AuthServiceMock()
  //
  lazy val restRoutes = completeEmpty
  //
  //  /**
  //   * Set up the database, add the good API keys
  //   */
  //  override def beforeAll() {
  //    super.beforeAll()
  //    Services.register(authMock)
  //  }
  //
  //  test("auth - user") {
  //    authMock.setAuthContext(userWithGodPerms)
  //    Get() ~> userAuthCreds ~> auth(AuthTypeSets.USER, perm) { authCtx =>
  //      completeEmpty
  //    } ~> check {
  //      debug(entity.asString)
  //      assert(status == StatusCodes.OK)
  //    }
  //  }
  //
  //  test("auth - secret key") {
  //    authMock.setAuthContext(keyWithGodPerms)
  //    Get() ~> keyAuthHeader ~> sealRoute(auth(AuthTypeSets.USER_OR_KEY, perm) { authCtx =>
  //      completeEmpty
  //    }) ~> check {
  //      debug(entity.asString)
  //      assert(status == StatusCodes.OK)
  //    }
  //  }
  //
  //  test("auth - publishable key") {
  //    authMock.setAuthContext(keyWithGodPerms)
  //    Get() ~> keyAuthHeader ~> sealRoute(auth(AuthTypeSets.USER_OR_KEY, perm) { authCtx =>
  //      completeEmpty
  //    }) ~> check {
  //      debug(entity.asString)
  //      assert(status == StatusCodes.OK)
  //    }
  //  }
  //
  //  test("auth - unexpected type") {
  //    authMock.setAuthContext(keyWithGodPerms)
  //    Get() ~> keyAuthHeader ~> sealRoute(auth(AuthTypeSets.USER, perm) { authCtx =>
  //      completeEmpty
  //    }) ~> check {
  //      debug(entity.asString)
  //      assert(status == StatusCodes.Forbidden)
  //    }
  //  }
  //
  //  test("auth - doesn't have perm") {
  //    authMock.setAuthContext(userWithPublicPerms)
  //    Get() ~> userAuthCreds ~> sealRoute(auth(AuthTypeSets.USER, perm) { authCtx =>
  //      completeEmpty
  //    }) ~> check {
  //      debug(entity.asString)
  //      assert(status == StatusCodes.Forbidden)
  //    }
  //  }
  //
  //  test("auth - no creds") {
  //    authMock.setAuthContext(userWithGodPerms)
  //    Get() ~> sealRoute(auth(AuthTypeSets.USER, perm) { authCtx =>
  //      completeEmpty
  //    }) ~> check {
  //      debug(entity.asString)
  //      assert(status == StatusCodes.Unauthorized)
  //    }
  //  }
  //
  //  test("auth - failure") {
  //    authMock.failNext()
  //    Get() ~> userAuthCreds ~> sealRoute(auth(AuthTypeSets.USER, perm) { authCtx =>
  //      completeEmpty
  //    }) ~> check {
  //      debug(entity.asString)
  //      assert(status == StatusCodes.Unauthorized)
  //    }
  //  }

}
