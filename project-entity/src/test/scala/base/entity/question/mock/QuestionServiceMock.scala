/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 1:10 PM
 */

package base.entity.question.mock

import java.util.UUID

import base.entity.error.ApiError
import base.entity.kv.Key.Pipeline
import base.entity.question.QuestionService
import base.entity.question.model.QuestionModel

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of QuestionServiceMock here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class QuestionServiceMock(getResult: Future[Either[ApiError, List[QuestionModel]]] = Future.successful(Right(List())))
    extends QuestionService {

  def getQuestions(groupId: UUID)(implicit p: Pipeline) = getResult

}
