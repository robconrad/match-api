/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:13 PM
 */

package base.entity.question

import base.entity.command.{ CommandService, CommandServiceCompanion }
import base.entity.perm.Perms
import base.entity.question.model.{ CreateQuestionModel, CreateQuestionResponseModel }

/**
 * CRUD, etc.
 * @author rconrad
 */
trait CreateQuestionCommandService extends CommandService[CreateQuestionModel, CreateQuestionResponseModel] {

  final val serviceManifest = manifest[CreateQuestionCommandService]

  final val perms = Set(Perms.CREATE_QUESTION)

}

object CreateQuestionCommandService extends CommandServiceCompanion[CreateQuestionCommandService]
