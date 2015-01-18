/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 11:24 PM
 */

package base.entity.question

import java.util.UUID

import base.common.service.{ Service, ServiceCompanion }
import base.entity.auth.context.AuthContext
import base.entity.error.ApiError
import base.entity.event.model.EventModel
import base.entity.kv.Key.Pipeline
import base.entity.question.model.{ AnswerModel, QuestionModel }

import scala.concurrent.Future

/**
 * CRUD, etc.
 * @author rconrad
 */
trait QuestionService extends Service {

  final def serviceManifest = manifest[QuestionService]

  def getQuestions(groupId: UUID)(implicit p: Pipeline,
                                  authCtx: AuthContext): Future[Either[ApiError, List[QuestionModel]]]

  def answer(input: AnswerModel)(implicit p: Pipeline,
                                 authCtx: AuthContext): Future[Either[ApiError, List[EventModel]]]

}

object QuestionService extends ServiceCompanion[QuestionService] {

}
