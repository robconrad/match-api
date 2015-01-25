/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/24/15 11:47 PM
 */

package base.entity.command

import base.common.service.Services
import base.entity.command.model.CommandModel

/**
 * A Command is a combination of an HTTP Verb and an endpoint - PostFoo, GetBar, PatchBaz, etc.
 * @author rconrad
 */
abstract class CommandServiceCompanion[T <: CommandService[_, _]](implicit m: Manifest[T]) {

  // todo make these an enum - possibly collapse in and out into same verb
  def inCmd: String
  def outCmd: Option[String]

  final def inCommand[R](model: R) = CommandModel[R](inCmd, model)
  final def outCommand[R](model: R) = CommandModel[R](outCmd.get, model)

  def apply() = Services.apply[T]

}
