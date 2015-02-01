/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 1:17 PM
 */

package base.entity.group

import base.entity.command.{ CommandService, CommandServiceCompanion }
import base.entity.group.model.{ DeclineInviteModel, DeclineInviteResponseModel }
import base.entity.perm.Perms

/**
 * CRUD, etc.
 * @author rconrad
 */
trait DeclineInviteCommandService extends CommandService[DeclineInviteModel, DeclineInviteResponseModel] {

  final val serviceManifest = manifest[DeclineInviteCommandService]

  final val perms = Set(Perms.DECLINE_INVITE)

}

object DeclineInviteCommandService extends CommandServiceCompanion[DeclineInviteCommandService]
