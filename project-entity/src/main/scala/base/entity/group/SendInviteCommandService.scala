/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 12:59 PM
 */

package base.entity.group

import base.entity.command.{CommandService, CommandServiceCompanion}
import base.entity.group.model.{SendInviteModel, SendInviteResponseModel}
import base.entity.perm.Perms

/**
 * CRUD, etc.
 * @author rconrad
 */
trait SendInviteCommandService extends CommandService[SendInviteModel, SendInviteResponseModel] {

  final val serviceManifest = manifest[SendInviteCommandService]

  final val perms = Set(Perms.INVITE)

}

object SendInviteCommandService extends CommandServiceCompanion[SendInviteCommandService]
