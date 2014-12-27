/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.rest.server

import base.common.lib.{ Actors, Dispatchable }
import base.common.logging.Loggable
import base.common.test.Tags
import base.rest.route.{ RouteTest, RestVersionsRoute }
import base.rest.test.RestBaseSuite
import org.json4s.DefaultFormats
import org.json4s.native.Serialization
import spray.client.pipelining._
import spray.httpx.encoding.Gzip

/**
 * Responsible for testing Server startup - highest level integration test possible
 * @author rconrad
 */
class ServerTest extends RestBaseSuite with Dispatchable with Loggable {

  implicit def json4sFormats = DefaultFormats

  test("server startup", Tags.SLOW) {
    implicit val system = Actors.actorSystem
    implicit val timeout = longTimeout

    val expected = Serialization.write(RestVersionsRoute.nakedResponse)

    Server.start()

    val pipeline = sendReceive
    val response = pipeline(Get(RestServerService().url)).awaitLong()

    assert(RouteTest.decompressed(response.entity) == expected)
  }

}
