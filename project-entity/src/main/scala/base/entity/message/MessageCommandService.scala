/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 9:49 AM
 */

package base.entity.message

import base.entity.command.{ CommandNames, CommandService, CommandServiceCompanion }
import base.entity.message.model.MessageModel
import base.entity.perm.Perms

/**
 * CRUD, etc.
 * @author rconrad
 */
trait MessageCommandService extends CommandService[MessageModel, Unit] {

  final val serviceManifest = manifest[MessageCommandService]

  final val perms = Set(Perms.MESSAGE)

}

object MessageCommandService extends CommandServiceCompanion[MessageCommandService]
