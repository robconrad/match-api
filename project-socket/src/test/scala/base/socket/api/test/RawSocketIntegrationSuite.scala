/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/15/15 10:39 AM
 */

package base.socket.api.test

import java.io.{ BufferedReader, InputStreamReader, PrintWriter }
import java.net.Socket

import base.socket.api.SocketApiService
import base.socket.api.impl.RawSocketApiHandlerServiceImpl

import scala.language.existentials

/**
 * Responsible for testing Server startup - highest level integration test possible
 * @author rconrad
 */
private[api] trait RawSocketIntegrationSuite extends IntegrationSuite {

  def handlerService = new RawSocketApiHandlerServiceImpl

  def makeSocket(props: SocketProperties) =
    new SocketConnection(props) {

      private var socket: Socket = _
      private var out: PrintWriter = _
      private var in: BufferedReader = _

      protected def _connect() = {
        socket = new Socket(SocketApiService().host, SocketApiService().port)
        socket.setSoTimeout(defaultTimeout.duration.toMillis.toInt)
        out = new PrintWriter(socket.getOutputStream, true)
        in = new BufferedReader(new InputStreamReader(socket.getInputStream))
        this
      }

      protected def _disconnect() = {
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
