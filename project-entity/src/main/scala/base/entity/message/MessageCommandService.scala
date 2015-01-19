/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 4:19 PM
 */

package base.entity.message

import base.entity.command.{ CommandService, CommandServiceCompanion }
import base.entity.event.model.EventModel
import base.entity.group.model.{ InviteModel, InviteResponseModel }
import base.entity.message.model.MessageModel
import base.entity.perm.Perms
import base.entity.user.model._

/**
 * CRUD, etc.
 * @author rconrad
 */
trait MessageCommandService extends CommandService[MessageModel, EventModel] {

  final val inCmd = MessageCommandService.inCmd
  final val outCmd = MessageCommandService.outCmd

  final val serviceManifest = manifest[MessageCommandService]

  final val perms = Set(Perms.MESSAGE)

}

object MessageCommandService extends CommandServiceCompanion[MessageCommandService] {

  final val inCmd = "message"
  final val outCmd = "messageResponse"

}
