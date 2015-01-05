/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/3/15 7:09 PM
 */

package base.socket.message

import base.socket.json.JsonFormats
import io.netty.channel.Channel
import org.json4s.native.Serialization
import org.json4s.{ Formats, JValue }

/**
 * Base message class, all messages sent throughout the system must inherit from this originally
 */
sealed trait Message

/**
 * Base class for all messages that have a command identifier
 */
sealed abstract class CommandMessage extends Message {
  val cmd: Command.Cmd
}

/**
 * Base class for all messages that originate with the server
 *  communications server->client
 */
sealed abstract class ServerMessage extends CommandMessage { val cmd: ServerCommand[_ <: ServerMessage] }
abstract class UserServerMessage extends ServerMessage { val cmd: UserServerCommand[_ <: UserServerMessage] }

/**
 * Base class for all messages that originate with the client
 *  communications client->server
 */
sealed abstract class ClientMessage extends CommandMessage { val cmd: ClientCommand[_ <: ClientMessage] }
abstract class UserClientMessage extends ClientMessage { val cmd: UserClientCommand[_ <: UserClientMessage] }

/**
 * Base class for all server->server control messages
 *  communications server->server
 */
sealed abstract class ControlMessage extends CommandMessage { val cmd: ControlCommand[_ <: ControlMessage] }
abstract class TestControlMessage extends ControlMessage { val cmd: TestControlCommand[_ <: TestControlMessage] }

/**
 * Base wrapper for all messages that originate with the client,
 *  associating them with the client's SessionContext
 */
abstract class ActionMessage extends Message {
  val channel: Channel
  val msg: ClientMessage
}

/**
 * Messages that interact with the user session
 */
trait UserSessionMessage extends Message

/**
 * Control commands for rooms
 */
trait RoomControlMessage extends Message

/**
 * Some messages require custom formats which are defined on their companion objects
 */
trait Formatted {
  implicit val formats: Formats
}
trait Extractable[T] extends Formatted {
  def extract(json: JValue): T
  // unfortunately we can't do the extract here since it needs a manifest and 'T' doesn't have one
}
trait Writable extends Formatted {
  def write(msg: CommandMessage) = {
    Serialization.write(msg)
  }
}

object Message {

  implicit val formats = JsonFormats.defaultWithCommands

  def write(msg: CommandMessage) = msg match {
    case msg: JsonServerMessage => msg.write()
    case _                      => Serialization.write(msg)
  }

}
