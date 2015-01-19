/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 4:19 PM
 */

package base.entity.question

import base.entity.command.{ CommandService, CommandServiceCompanion }
import base.entity.event.model.EventModel
import base.entity.perm.Perms
import base.entity.question.model.AnswerModel

/**
 * CRUD, etc.
 * @author rconrad
 */
trait AnswerCommandService extends CommandService[AnswerModel, List[EventModel]] {

  final val inCmd = AnswerCommandService.inCmd
  final val outCmd = AnswerCommandService.outCmd

  final val serviceManifest = manifest[AnswerCommandService]

  final val perms = Set(Perms.QUESTIONS)

}

object AnswerCommandService extends CommandServiceCompanion[AnswerCommandService] {

  final val inCmd = "answer"
  final val outCmd = "answerResponse"

}
