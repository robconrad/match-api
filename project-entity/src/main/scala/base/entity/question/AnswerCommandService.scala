/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 9:48 AM
 */

package base.entity.question

import base.entity.command.{ CommandNames, CommandService, CommandServiceCompanion }
import base.entity.perm.Perms
import base.entity.question.model.AnswerModel

/**
 * CRUD, etc.
 * @author rconrad
 */
trait AnswerCommandService extends CommandService[AnswerModel, Unit] {

  final val serviceManifest = manifest[AnswerCommandService]

  final val perms = Set(Perms.QUESTIONS)

}

object AnswerCommandService extends CommandServiceCompanion[AnswerCommandService]
