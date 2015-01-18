/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 2:12 PM
 */

package base.entity.command

import base.common.service.Services
import base.entity.command.model.CommandModel

/**
 * A Command is a combination of an HTTP Verb and an endpoint - PostFoo, GetBar, PatchBaz, etc.
 * @author rconrad
 */
abstract class CommandServiceCompanion[T <: CommandService[_, _]](implicit m: Manifest[T]) {

  def inCmd: String
  def outCmd: String

  final def inCommand[R](model: R) = CommandModel[R](inCmd, model)
  final def outCommand[R](model: R) = CommandModel[R](outCmd, model)

  def apply() = Services.apply[T]

}
