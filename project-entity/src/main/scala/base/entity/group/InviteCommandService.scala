/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 9:49 AM
 */

package base.entity.group

import base.entity.command.{ CommandNames, CommandService, CommandServiceCompanion }
import base.entity.group.model.{ InviteModel, InviteResponseModel }
import base.entity.perm.Perms

/**
 * CRUD, etc.
 * @author rconrad
 */
trait InviteCommandService extends CommandService[InviteModel, InviteResponseModel] {

  final val serviceManifest = manifest[InviteCommandService]

  final val perms = Set(Perms.INVITE)

}

object InviteCommandService extends CommandServiceCompanion[InviteCommandService]
