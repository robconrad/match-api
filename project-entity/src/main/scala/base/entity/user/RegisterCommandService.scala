/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 9:49 AM
 */

package base.entity.user

import base.entity.command.{ CommandNames, CommandService, CommandServiceCompanion }
import base.entity.perm.Perms
import base.entity.user.model.{ RegisterModel, RegisterResponseModel }

/**
 * User CRUD, etc.
 * @author rconrad
 */
trait RegisterCommandService extends CommandService[RegisterModel, RegisterResponseModel] {

  final val serviceManifest = manifest[RegisterCommandService]

  final val perms = Set(Perms.REGISTER)

}

object RegisterCommandService extends CommandServiceCompanion[RegisterCommandService]
