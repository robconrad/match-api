/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 4:20 PM
 */

package base.socket.api.impl

import base.common.lib.{ Actors, Dispatchable }
import base.common.logging.Loggable
import base.common.test.Tags
import base.socket.api.SocketApiService
import base.socket.test.SocketBaseSuite
import org.json4s.DefaultFormats

/**
 * Responsible for testing Server startup - highest level integration test possible
 * @author rconrad
 */
class SocketApiServiceImplTest extends SocketBaseSuite with Dispatchable with Loggable {

  implicit def json4sFormats = DefaultFormats

  test("server startup", Tags.SLOW) {
    implicit val system = Actors.actorSystem
    implicit val timeout = longTimeout

    assert(SocketApiService().start().await())

    fail("need to connect to server and send a heartbeat or ping/pong or something")
  }

}
