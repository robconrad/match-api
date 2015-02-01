/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 1:00 PM
 */

package base.entity.group

import base.entity.command.{CommandService, CommandServiceCompanion}
import base.entity.group.model.{AcceptInviteModel, AcceptInviteResponseModel}
import base.entity.perm.Perms

/**
 * CRUD, etc.
 * @author rconrad
 */
trait AcceptInviteCommandService extends CommandService[AcceptInviteModel, AcceptInviteResponseModel] {

  final val serviceManifest = manifest[AcceptInviteCommandService]

  final val perms = Set(Perms.ACCEPT_INVITE)

}

object AcceptInviteCommandService extends CommandServiceCompanion[AcceptInviteCommandService]
