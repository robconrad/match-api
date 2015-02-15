/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 12:34 PM
 */

package base.socket.api.test.command

import base.common.logging.Loggable
import base.entity.command.model.CommandModel
import base.entity.json.JsonFormats
import base.socket.api.test.SocketConnection
import base.socket.test.SocketBaseSuite
import org.json4s.jackson.JsonMethods
import org.json4s.native.Serialization

/**
 * {{ Describe the high level purpose of CommandExecutor here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class CommandExecutor extends SocketBaseSuite with Loggable {

  private implicit val formats = JsonFormats.withModels

  def apply[A, B](model: A, responseModel: Option[B])(implicit mA: Manifest[A],
                                                      mB: Manifest[B],
                                                      socket: SocketConnection) {
    val command = CommandModel(model)
    val json = Serialization.write(command)

    socket.write(json)

    responseModel foreach { responseModel =>
      assertResponse(responseModel)
    }
  }

  def assertResponse[B](responseModel: B)(implicit m: Manifest[B], socket: SocketConnection) {
    val rawResponse = socket.read

    val expectedResponse = CommandModel(responseModel)
    val actualResponse = JsonMethods.parse(rawResponse)

    debug(socket.hashCode() + "   pretty actual:\n" + Serialization.writePretty(JsonMethods.parse(rawResponse)))
    debug(socket.hashCode() + " pretty expected:\n" + Serialization.writePretty(expectedResponse))

    val extractedResponse = actualResponse.extract[CommandModel[B]]

    debug(socket.hashCode() + "   actual: " + extractedResponse.toString)
    debug(socket.hashCode() + " expected: " + expectedResponse.toString)

    assert(extractedResponse == expectedResponse)
  }

}
