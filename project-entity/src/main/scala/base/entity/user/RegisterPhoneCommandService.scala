/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 9:34 AM
 */

package base.entity.user

import base.entity.command.{ CommandService, CommandServiceCompanion }
import base.entity.perm.Perms
import base.entity.user.model.{ RegisterPhoneModel, RegisterPhoneResponseModel }

/**
 * User CRUD, etc.
 * @author rconrad
 */
trait RegisterPhoneCommandService extends CommandService[RegisterPhoneModel, RegisterPhoneResponseModel] {

  final val serviceManifest = manifest[RegisterPhoneCommandService]

  final val perms = Set(Perms.REGISTER)

}

object RegisterPhoneCommandService extends CommandServiceCompanion[RegisterPhoneCommandService]
