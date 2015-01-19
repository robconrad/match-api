/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 4:19 PM
 */

package base.entity.user

import base.entity.command.{ CommandService, CommandServiceCompanion }
import base.entity.perm.Perms
import base.entity.user.model.{ RegisterModel, RegisterResponseModel }

/**
 * User CRUD, etc.
 * @author rconrad
 */
trait RegisterCommandService extends CommandService[RegisterModel, RegisterResponseModel] {

  final val inCmd = RegisterCommandService.inCmd
  final val outCmd = RegisterCommandService.outCmd

  final val serviceManifest = manifest[RegisterCommandService]

  final val perms = Set(Perms.REGISTER)

}

object RegisterCommandService extends CommandServiceCompanion[RegisterCommandService] {

  final val inCmd = "register"
  final val outCmd = "registerResponse"

}
