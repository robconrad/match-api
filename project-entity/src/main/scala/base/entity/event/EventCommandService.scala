/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 12:19 AM
 */

package base.entity.group

import base.entity.command.{ CommandService, CommandServiceCompanion }
import base.entity.event.model.EventModel
import base.entity.group.model.{ InviteModel, InviteResponseModel }
import base.entity.perm.Perms
import base.entity.user.model._

/**
 * CRUD, etc.
 * @author rconrad
 */
// todo should this even exist? it's a weird thing with no impl
trait EventCommandService extends CommandService[EventModel, EventModel] {

  final val inCmd = EventCommandService.inCmd
  final val outCmd = EventCommandService.outCmd

  final val serviceManifest = manifest[EventCommandService]

  final val perms = Set()

}

object EventCommandService extends CommandServiceCompanion[EventCommandService] {

  final val inCmd = "event"
  final val outCmd = Option("event")

}
