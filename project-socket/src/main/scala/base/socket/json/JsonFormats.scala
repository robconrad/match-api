/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/3/15 6:43 PM
 */

package base.socket.json

import base.socket.message.CommandSerializer
import org.json4s.DefaultFormats

/**
 * {{ Describe the high level purpose of JsonFormats here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object JsonFormats {

  val default = DefaultFormats

  val defaultWithCommands = DefaultFormats + new CommandSerializer

}
