/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 3/22/15 6:38 PM
 */

package base.entity.event

import base.entity.command.{ CommandService, CommandServiceCompanion }
import base.entity.event.model.{ AckEventsResponseModel, AckEventsModel }
import base.entity.event.model.impl.AckEventsResponseModelImpl
import base.entity.perm.Perms

/**
 * CRUD, etc.
 * @author rconrad
 */
trait AckEventsCommandService extends CommandService[AckEventsModel, AckEventsResponseModel] {

  final val serviceManifest = manifest[AckEventsCommandService]

  final val perms = Set(Perms.ACK_EVENTS)

}

object AckEventsCommandService extends CommandServiceCompanion[AckEventsCommandService]
