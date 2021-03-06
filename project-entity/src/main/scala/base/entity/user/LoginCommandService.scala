/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:13 PM
 */

package base.entity.user

import base.entity.command.{ CommandService, CommandServiceCompanion }
import base.entity.perm.Perms
import base.entity.user.model._

/**
 * User CRUD, etc.
 * @author rconrad
 */
trait LoginCommandService extends CommandService[LoginModel, LoginResponseModel] {

  final val serviceManifest = manifest[LoginCommandService]

  final val perms = Set(Perms.LOGIN)

}

object LoginCommandService extends CommandServiceCompanion[LoginCommandService]
