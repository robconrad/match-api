/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 2:16 PM
 */

package base.entity.group

import base.entity.command.{ CommandService, CommandServiceCompanion }
import base.entity.group.model.{ InviteModel, InviteResponseModel }
import base.entity.perm.Perms
import base.entity.user.model._

/**
 * CRUD, etc.
 * @author rconrad
 */
trait InviteCommandService extends CommandService[InviteModel, InviteResponseModel] {

  final def inCmd = InviteCommandService.inCmd
  final def outCmd = InviteCommandService.outCmd

  final def serviceManifest = manifest[InviteCommandService]

  final val perms = Set(Perms.INVITE)

}

object InviteCommandService extends CommandServiceCompanion[InviteCommandService] {

  final val inCmd = "invite"
  final val outCmd = "inviteResponse"

}
