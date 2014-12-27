/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 6:36 PM
 */

package base.rest.route

import base.rest.test.RestBaseSuite
import spray.http.HttpHeaders.{ `Access-Control-Allow-Headers`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Origin` }
import spray.http.{ HttpEntity, HttpResponse, StatusCodes }
import spray.httpx.encoding.Gzip
import spray.testkit.ScalatestRouteTest

/**
 * Base route test class, sets up services etc.
 * @author rconrad
 */
private[rest] abstract class RouteTest(companion: RouteTestCompanion)
    extends RestBaseSuite with ScalatestRouteTest with BaseRoute with RoutingHandlers {

  lazy val sealedRoute = sealRoute(routes)

  def actorRefFactory = system

  def decompressed = RouteTest.decompressed(body)

  test(s"Companion ${companion.getClass.getSimpleName} has endpoints") {
    companion.assertEndpointsLength()
  }

  /**
   * Ensure that an OPTIONS request to the endpoint(s) for this route will always return
   *  OK with the cors headers - all endpoints must support CORS OPTIONS
   */
  companion.CORS_ENDPOINTS.foreach { endpoint =>
    test(s"CORS options $endpoint") {
      Options(endpoint) ~> sealedRoute ~> check {
        assert(status == StatusCodes.OK)
        assertCorsHeaders(response)
      }
    }
  }

  /**
   * Assert that the response contains all CORS headers. Does not check content at this time
   *  as all headers are static. If headers become dynamic (e.g. due to origins whitelist, etc.),
   *  this should be updated to check specific responses.
   */
  def assertCorsHeaders(response: HttpResponse) {
    val allowOrigin = response.headers.find(_.is(`Access-Control-Allow-Origin`.lowercaseName))
    val allowMethods = response.headers.find(_.is(`Access-Control-Allow-Methods`.lowercaseName))
    val allowHeaders = response.headers.find(_.is(`Access-Control-Allow-Headers`.lowercaseName))

    // Allow-Origin is set to "*" until we implement a system for domain filtering based on providers
    assert(allowOrigin.nonEmpty, "CORS allowOrigin empty")
    assert(allowOrigin.exists(_.value == "*"), "CORS allowOrigin is *")

    // Allow-Methods should always allow pretty much any method
    assert(allowMethods.nonEmpty, "CORS allowMethods empty")
    val expectedMethods = List("GET", "POST", "PUT", "OPTIONS", "DELETE")
    expectedMethods.foreach(method =>
      assert(allowMethods.exists(_.value.contains(method)), s"CORS allowMethods contains $method"))

    // Allow-Headers must contain our custom auth headers
    assert(allowHeaders.nonEmpty, "CORS allowHeaders empty")
  }

}

object RouteTest {

  def decompressed(body: HttpEntity) = new String(Gzip.newDecompressor.decompress(body.data.toByteArray))

}
