/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 10:25 PM
 */

package base.entity.question

import java.util.UUID

import base.common.service.{ Service, ServiceCompanion }
import base.entity.auth.context.ChannelContext
import base.entity.error.model.ApiError
import base.entity.event.model.EventModel
import base.entity.question.model.{ AnswerModel, QuestionModel }

import scala.concurrent.Future

/**
 * CRUD, etc.
 * @author rconrad
 */
trait QuestionService extends Service {

  final val serviceManifest = manifest[QuestionService]

  def getQuestions(groupId: UUID,
                   userId: UUID)(implicit channelCtx: ChannelContext): Future[Either[ApiError, List[QuestionModel]]]

  def answer(input: AnswerModel)(implicit channelCtx: ChannelContext): Future[Either[ApiError, List[EventModel]]]

}

object QuestionService extends ServiceCompanion[QuestionService] {

}
