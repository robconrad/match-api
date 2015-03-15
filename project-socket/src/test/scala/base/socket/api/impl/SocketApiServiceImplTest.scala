/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/15/15 9:59 AM
 */

package base.socket.api.impl

import java.net.ConnectException

import base.common.service.Services
import base.common.test.Tags
import base.socket.api.SocketApiHandlerService
import base.socket.api.mock.SocketApiStatsServiceMock
import base.socket.service.SocketServiceTest

import scala.concurrent.duration._

/**
 * Responsible for testing Server startup - highest level integration test possible
 * @author rconrad
 */
abstract class SocketApiServiceImplTest extends SocketServiceTest {

  protected val host = "0.0.0.0"
  protected val port = 9999
  private val connectionsAllowed = 10
  private val stopSleep = 1.millis
  private val shutdownTimeout = 10.seconds
  private val idleTimeout = 10.seconds

  val service = new SocketApiServiceImpl(host, port, connectionsAllowed, stopSleep, shutdownTimeout, idleTimeout)

  protected val response = "a response"
  private val connections = 1

  override def beforeAll() {
    super.beforeAll()
    Services.register(makeMock)
    Services.register(new SocketApiStatsServiceMock(connections))
  }

  def makeMock: SocketApiHandlerService

  def assertResponse()

  test("isConnectionAllowed") {
    assert(new SocketApiServiceImpl(
      host, port, connectionsAllowed, stopSleep, shutdownTimeout, idleTimeout).isConnectionAllowed)

    val noConnections = 0
    assert(!new SocketApiServiceImpl(
      host, port, noConnections, stopSleep, shutdownTimeout, idleTimeout).isConnectionAllowed)
  }

  test("start / stop", Tags.SLOW) {
    intercept[ConnectException] {
      assertResponse()
    }

    assert(service.start().await())

    assertResponse()

    assert(service.stop().await())

    intercept[ConnectException] {
      assertResponse()
    }
  }

}
