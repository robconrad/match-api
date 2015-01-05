/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/3/15 6:50 PM
 */

package base.socket.message

import org.json4s.JsonAST._
import org.json4s.native.JsonMethods._

/**
 * Base class for non-case-class server messages, handles apiVersion customization
 */
trait JsonServerMessage extends JsonServerMessageImplicits {

  // latest version conversion from message to JValue
  protected val getJson: () => JValue
  // write message out to compacted json string
  final def write() = {
    compact(render(getJson()))
  }

  override def toString = write()
}

/**
 * Base class for JsonServerMessage companion objects. Should include any static values.
 */
abstract class JsonServerMessageObject extends JsonServerMessageImplicits
