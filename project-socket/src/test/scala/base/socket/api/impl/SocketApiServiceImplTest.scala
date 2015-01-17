/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 1:23 PM
 */

package base.socket.api.impl

import java.io.{ BufferedReader, InputStreamReader, PrintWriter }
import java.net.{ ConnectException, Socket }

import base.common.service.Services
import base.common.test.Tags
import base.socket.api.mock.{ SocketApiStatsServiceMock, SocketApiHandlerServiceMock }
import base.socket.service.SocketServiceTest

import scala.concurrent.duration._

/**
 * Responsible for testing Server startup - highest level integration test possible
 * @author rconrad
 */
class SocketApiServiceImplTest extends SocketServiceTest {

  private val host = "0.0.0.0"
  private val port = 9999
  private val connectionsAllowed = 10
  private val stopSleep = 1.millis
  private val shutdownTimeout = 10.seconds

  val service = new SocketApiServiceImpl(host, port, connectionsAllowed, stopSleep, shutdownTimeout)

  private val response = "a response"
  private val connections = 1

  override def beforeAll() {
    super.beforeAll()
    Services.register(new SocketApiHandlerServiceMock(channelReadResponse = Option(response)))
    Services.register(new SocketApiStatsServiceMock(connections))
  }

  private def makeSocket(message: Any): BufferedReader = {
    val socket = new Socket(host, port)
    socket.setSoTimeout(timeout.duration.toMillis.toInt)

    val out = new PrintWriter(socket.getOutputStream, true)
    val in = new BufferedReader(new InputStreamReader(socket.getInputStream))

    out.write(message.toString + "\r\n")
    out.flush()

    in
  }

  test("isConnectionAllowed") {
    assert(new SocketApiServiceImpl(host, port, connectionsAllowed, stopSleep, shutdownTimeout).isConnectionAllowed)

    val noConnections = 0
    assert(!new SocketApiServiceImpl(host, port, noConnections, stopSleep, shutdownTimeout).isConnectionAllowed)
  }

  test("start / stop", Tags.SLOW) {
    intercept[ConnectException] {
      makeSocket(response)
    }

    assert(service.start().await())

    assert(makeSocket(response).readLine() == response)

    assert(service.stop().await())

    intercept[ConnectException] {
      makeSocket(response)
    }
  }

}
