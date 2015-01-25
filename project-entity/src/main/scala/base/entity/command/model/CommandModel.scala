/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 9:34 AM
 */

package base.entity.command.model

import base.entity.command.CommandNames
import base.entity.command.CommandNames.CommandName

/**
 * {{ Describe the high level purpose of CommandModel here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
case class CommandModel[T] private[model] (cmd: CommandName, body: T)

object CommandModel {

  def apply[T](body: T)(implicit m: Manifest[T]): CommandModel[T] = {
    val cmd = CommandNames.findByType[T].getOrElse(throw new RuntimeException(s"command not found for type: $body"))
    apply(cmd, body)
  }

}
