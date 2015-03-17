/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/15/15 10:50 AM
 */

package base.socket.api

import base.common.service.Services
import base.common.test.Tags
import base.socket.api.impl.{ SocketApiServiceImpl, SocketChannelInitializer }
import base.socket.api.test.IntegrationSuite

import scala.concurrent.duration._

/**
 * {{ Describe the high level purpose of WebSocketApiTimeoutTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
private[api] abstract class SocketApiTimeoutTest extends IntegrationSuite {

  private val connectionsAllowed = 100

  override def beforeAll(): Unit = {
    val apiService = new SocketApiServiceImpl(
      SocketApiService().host,
      SocketApiService().port,
      connectionsAllowed,
      stopSleep = 10.seconds,
      shutdownTimeout = 10.seconds,
      idleTimeout = 1.second)
    Services.register(apiService)
    super.beforeAll()
  }

  test("timeout", Tags.SLOW) {
    implicit val socket = makeSocket().connect()
    executor.assertResponse(SocketChannelInitializer.Errors.idleError)
    assert(!socket.isActive)
    socket.disconnect()
  }

}
