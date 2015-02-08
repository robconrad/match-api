/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 3:52 PM
 */

package base.socket.api

import java.io.{ BufferedReader, InputStreamReader, PrintWriter }
import java.net.Socket

import base.socket.api.impl.RawSocketApiHandlerServiceImpl
import base.socket.api.test.{SocketProperties, SocketConnection}

/**
 * Responsible for testing Server startup - highest level integration test possible
 * @author rconrad
 */
class RawSocketApiIntegrationTest extends SocketApiIntegrationTest {

  def handlerService = new RawSocketApiHandlerServiceImpl

  def makeSocket(props: SocketProperties) =
    new SocketConnection(props) {

      private var socket: Socket = _
      private var out: PrintWriter = _
      private var in: BufferedReader = _

      def connect() = {
        socket = new Socket(SocketApiService().host, SocketApiService().port)
        socket.setSoTimeout(defaultTimeout.duration.toMillis.toInt)
        out = new PrintWriter(socket.getOutputStream, true)
        in = new BufferedReader(new InputStreamReader(socket.getInputStream))
        this
      }

      def disconnect() = {
        socket.close()
        this
      }

      def read = {
        in.readLine()
      }

      def write(json: String) {
        out.write(json + "\r\n")
        out.flush()
      }

      def isActive = socket.isConnected

    }

}
