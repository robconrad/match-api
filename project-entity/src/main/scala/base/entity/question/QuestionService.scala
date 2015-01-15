/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 1:10 PM
 */

package base.entity.question

import java.util.UUID

import base.common.service.{ Service, ServiceCompanion }
import base.entity.error.ApiError
import base.entity.event.model.EventModel
import base.entity.kv.Key.Pipeline
import base.entity.question.model.QuestionModel

import scala.concurrent.Future

/**
 * CRUD, etc.
 * @author rconrad
 */
trait QuestionService extends Service {

  final def serviceManifest = manifest[QuestionService]

  def getQuestions(groupId: UUID)(implicit p: Pipeline): Future[Either[ApiError, List[QuestionModel]]]

}

object QuestionService extends ServiceCompanion[QuestionService] {

}
