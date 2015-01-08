/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/7/15 10:51 PM
 */

package base.socket.api.impl

import java.io.{ InputStreamReader, BufferedReader, PrintWriter }
import java.net.Socket

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

    assert(SocketApiService().start().await())

    val socket = new Socket(SocketApiService().host, SocketApiService().port)
    socket.setSoTimeout(timeout.duration.toMillis.toInt)

    val out = new PrintWriter(socket.getOutputStream, true)
    val in = new BufferedReader(new InputStreamReader(socket.getInputStream))

    val cmd = "{\"cmd\":\"register\"," +
      "\"body\":{\"apiVersion\":\"0.1\",\"name\":\"Bob\",\"gender\":\"male\",\"phone\":\"555-343-1231\"}}\r\n"

    out.write(cmd)
    out.flush()

    assert(in.readLine().contains("not implemented"))
  }

}
