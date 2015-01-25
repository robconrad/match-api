/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/24/15 9:19 PM
 */

package base.socket.api

import java.io.{ BufferedReader, InputStreamReader, PrintWriter }
import java.net.Socket

import base.socket.api.impl.RawSocketApiHandlerServiceImpl
import base.socket.api.test.SocketConnection

/**
 * Responsible for testing Server startup - highest level integration test possible
 * @author rconrad
 */
class RawSocketApiIntegrationTest extends SocketApiIntegrationTest {

  def handlerService = new RawSocketApiHandlerServiceImpl

  def connect() = {
    val socket = new Socket(SocketApiService().host, SocketApiService().port)
    socket.setSoTimeout(defaultTimeout.duration.toMillis.toInt)

    val out = new PrintWriter(socket.getOutputStream, true)
    val in = new BufferedReader(new InputStreamReader(socket.getInputStream))

    new SocketConnection {

      def disconnect() {
        socket.close()
      }

      def read = {
        in.readLine()
      }

      def write(json: String) {
        out.write(json + "\r\n")
        out.flush()
      }

    }
  }

}
