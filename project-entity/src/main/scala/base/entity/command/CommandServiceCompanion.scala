/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 12:15 PM
 */

package base.entity.command

import base.common.service.Services

/**
 * A Command is a combination of an HTTP Verb and an endpoint - PostFoo, GetBar, PatchBaz, etc.
 * @author rconrad
 */
abstract class CommandServiceCompanion[T <: CommandService[_, _]](implicit m: Manifest[T]) {

  def apply() = Services.apply[T]

}
