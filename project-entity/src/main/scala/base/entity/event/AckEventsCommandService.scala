/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 5:46 PM
 */

package base.entity.event

import base.entity.command.{CommandService, CommandServiceCompanion}
import base.entity.event.model.AckEventsModel
import base.entity.perm.Perms

/**
 * CRUD, etc.
 * @author rconrad
 */
trait AckEventsCommandService extends CommandService[AckEventsModel, Unit] {

  final val serviceManifest = manifest[AckEventsCommandService]

  final val perms = Set(Perms.ACK_EVENTS)

}

object AckEventsCommandService extends CommandServiceCompanion[AckEventsCommandService]
