/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 9:24 PM
 */

package base.entity.question

import base.entity.command.{ CommandService, CommandServiceCompanion }
import base.entity.perm.Perms
import base.entity.question.model.AnswerModel

/**
 * CRUD, etc.
 * @author rconrad
 */
trait AnswerCommandService extends CommandService[AnswerModel, Unit] {

  final val serviceManifest = manifest[AnswerCommandService]

  final val perms = Set(Perms.ANSWER)

}

object AnswerCommandService extends CommandServiceCompanion[AnswerCommandService]
