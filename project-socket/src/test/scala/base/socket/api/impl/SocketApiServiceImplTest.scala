/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 2:40 PM
 */

package base.socket.api.impl

import java.io.{ BufferedReader, InputStreamReader, PrintWriter }
import java.net.Socket

import base.common.lib.{ Actors, Dispatchable, Genders }
import base.common.logging.Loggable
import base.common.test.Tags
import base.entity.api.ApiVersions
import base.entity.json.JsonFormats
import base.entity.user.model.RegisterModel
import base.socket.api.SocketApiService
import base.socket.model.CommandModel
import base.socket.test.SocketBaseSuite
import org.json4s.native.Serialization

/**
 * Responsible for testing Server startup - highest level integration test possible
 * @author rconrad
 */
class SocketApiServiceImplTest extends SocketBaseSuite with Dispatchable with Loggable {

  implicit def json4sFormats = JsonFormats.withEnumsAndFields

  test("server startup", Tags.SLOW) {
    implicit val system = Actors.actorSystem

    try {
      assert(SocketApiService().start().await())

      val socket = new Socket(SocketApiService().host, SocketApiService().port)
      socket.setSoTimeout(timeout.duration.toMillis.toInt)

      val out = new PrintWriter(socket.getOutputStream, true)
      val in = new BufferedReader(new InputStreamReader(socket.getInputStream))

      val cmd = CommandModel("register", RegisterModel(ApiVersions.V01, "bob", Genders.male, "555-5555"))
      val json = Serialization.write(cmd) + "\r\n"

      out.write(json)
      out.flush()

      assert(in.readLine().contains("not implemented"))

      assert(SocketApiService().stop().await())
    } catch {
      case e: Exception =>
        SocketApiService().stop().await()
        throw e
    }
  }

}
