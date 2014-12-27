/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/25/14 10:12 AM
 */

package base.rest.apiKey

import base.common.service.{ Services, ServicesBeforeAndAfterAll }
import base.entity.apiKey.ApiKeyTypes
import base.entity.apiKey.mock.ApiKeysServiceMock
import base.entity.apiKey.model.ApiKeys
import base.entity.auth.context.AuthContextDataFactory
import base.entity.auth.mock.AuthServiceMock
import base.rest.Endpoint._
import base.rest.Versions
import base.rest.route.{ RouteTest, RouteTestCompanion }
import org.json4s.native.Serialization
import spray.http.StatusCodes

/**
 * Tests Keys endpoint in isolation
 * @author rconrad
 */
class ApiKeysRouteTest extends RouteTest(ApiKeysRouteTest) with ApiKeysRoute with ServicesBeforeAndAfterAll {

  val version = Versions.latest

  private implicit val authContext = AuthContextDataFactory.emptyKeyAuth

  private val authMock = new AuthServiceMock(Option(authContext))
  private val keysMock = new ApiKeysServiceMock()

  private val keysResponseJson = Serialization.write(ApiKeys(List()))

  /**
   * Set up the database, add the good API keys
   */
  override def beforeAll() {
    super.beforeAll()
    Services.register(authMock)
    Services.register(keysMock)
  }

  test("get keys") {
    Get(API_KEYS) ~> addHeader(ApiKeyTypes.API.header, "foo") ~> sealedRoute ~> check {
      assert(status == StatusCodes.OK)
      assert(decompressed == keysResponseJson, "good response returned")
      assertCorsHeaders(response)
    }
  }

  test("get keys (no auth)") {
    Get(API_KEYS) ~> sealedRoute ~> check {
      assert(status == StatusCodes.Unauthorized, "status code")
      assertCorsHeaders(response)
    }
  }

  test("refresh keys") {
    Put(API_KEYS) ~> addHeader(ApiKeyTypes.API.header, "foo") ~> sealedRoute ~> check {
      assert(status == StatusCodes.OK)
      assert(decompressed == keysResponseJson, "good response returned")
      assertCorsHeaders(response)
    }
  }

  test("refresh keys (no auth)") {
    Put(API_KEYS) ~> sealedRoute ~> check {
      assert(status == StatusCodes.Unauthorized, "status code")
      assertCorsHeaders(response)
    }
  }

}

object ApiKeysRouteTest extends RouteTestCompanion {

  val CORS_ENDPOINTS = List[String](API_KEYS)

}
