/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.rest.route

import base.common.service.ServicesBeforeAndAfterAll
import base.rest.Endpoint._
import base.rest.{ Locations, VersionedEndpoint }
import org.json4s.native.Serialization
import spray.http.StatusCodes

/**
 * Test the discoverability routes for the rest api
 * @author rconrad
 */
class RestVersionsRouteTest
    extends RouteTest(RestVersionsRouteTest) with RestVersionsRoute with ServicesBeforeAndAfterAll {

  test("naked") {
    Get() ~> sealedRoute ~> check {
      assert(status == StatusCodes.OK)
      assert(decompressed == Serialization.write(RestVersionsRoute.nakedResponse))
    }
  }

  test("versions") {
    Get("/" + Locations.REST) ~> sealedRoute ~> check {
      assert(status == StatusCodes.OK)
      assert(decompressed == Serialization.write(RestVersionsRoute.versionsResponse))
    }
  }

  test("endpoints") {
    VersionedEndpoint.available.foreach {
      case (version, _) =>
        Get(VersionedEndpoint(version, "").toString) ~> sealedRoute ~> check {
          assert(status == StatusCodes.OK)
          assert(decompressed == Serialization.write(RestVersionsRoute.endpointsResponses(version)))
        }
    }
  }

}

private object RestVersionsRouteTest extends RouteTestCompanion {

  val CORS_ENDPOINTS = List[String](
    // naked (aka no route)
    "/",
    // versions (aka pathBaseTop)
    "/" + Locations.REST,
    // endpoints (i.e. includes latestVersion because it's an Endpoint)
    REST
  )

}
