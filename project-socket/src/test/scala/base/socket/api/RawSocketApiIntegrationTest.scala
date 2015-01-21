/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/20/15 10:58 PM
 */

package base.socket.api

import java.io.{ BufferedReader, InputStreamReader, PrintWriter }
import java.net.Socket

import base.socket.api.impl.RawSocketApiHandlerServiceImpl

/**
 * Responsible for testing Server startup - highest level integration test possible
 * @author rconrad
 */
class RawSocketApiIntegrationTest extends SocketApiIntegrationTest {

  var socket: Socket = _
  var out: PrintWriter = _
  var in: BufferedReader = _

  def handlerService = new RawSocketApiHandlerServiceImpl

  def connect() {
    socket = new Socket(SocketApiService().host, SocketApiService().port)
    socket.setSoTimeout(defaultTimeout.duration.toMillis.toInt)

    out = new PrintWriter(socket.getOutputStream, true)
    in = new BufferedReader(new InputStreamReader(socket.getInputStream))
  }

  def disconnect() {
    socket.close()
  }

  def writeRead(json: String) = {
    out.write(json + "\r\n")
    out.flush()
    in.readLine()
  }

}
