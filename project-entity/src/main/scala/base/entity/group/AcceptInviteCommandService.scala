/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 1:17 PM
 */

package base.entity.group

import base.entity.command.{ CommandService, CommandServiceCompanion }
import base.entity.group.model.{ AcceptInviteModel, InviteResponseModel }
import base.entity.perm.Perms

/**
 * CRUD, etc.
 * @author rconrad
 */
trait AcceptInviteCommandService extends CommandService[AcceptInviteModel, InviteResponseModel] {

  final val serviceManifest = manifest[AcceptInviteCommandService]

  final val perms = Set(Perms.ACCEPT_INVITE)

}

object AcceptInviteCommandService extends CommandServiceCompanion[AcceptInviteCommandService]
