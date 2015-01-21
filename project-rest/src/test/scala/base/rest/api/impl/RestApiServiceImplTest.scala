/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/20/15 10:00 PM
 */

package base.rest.api.impl

import base.common.lib.{ Actors, Dispatchable }
import base.common.logging.Loggable
import base.common.test.Tags
import base.rest.api.RestApiService
import base.rest.route.{ RestVersionsRoute, RouteTest }
import base.rest.test.RestBaseSuite
import org.json4s.DefaultFormats
import org.json4s.native.Serialization
import spray.client.pipelining._

/**
 * Responsible for testing Server startup - highest level integration test possible
 * @author rconrad
 */
class RestApiServiceImplTest extends RestBaseSuite with Dispatchable with Loggable {

  implicit def json4sFormats = DefaultFormats

  test("server startup", Tags.SLOW) {
    implicit val system = Actors.actorSystem
    implicit val defaultTimeout = longTimeout

    val expected = Serialization.write(RestVersionsRoute.nakedResponse)

    assert(RestApiService().start().await())

    val pipeline = sendReceive
    val response = pipeline(Get(RestApiService().url)).awaitLong()

    assert(RouteTest.decompressed(response.entity) == expected)
  }

}
