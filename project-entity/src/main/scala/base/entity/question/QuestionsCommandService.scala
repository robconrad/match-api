/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:13 PM
 */

package base.entity.question

import base.entity.command.{ CommandService, CommandServiceCompanion }
import base.entity.perm.Perms
import base.entity.question.model.{ QuestionsModel, QuestionsResponseModel }

/**
 * CRUD, etc.
 * @author rconrad
 */
trait QuestionsCommandService extends CommandService[QuestionsModel, QuestionsResponseModel] {

  final val serviceManifest = manifest[QuestionsCommandService]

  final val perms = Set(Perms.QUESTIONS)

}

object QuestionsCommandService extends CommandServiceCompanion[QuestionsCommandService]
