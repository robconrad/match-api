/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 3:14 PM
 */

package base.entity.question

import java.util.UUID

import base.common.service.{ Service, ServiceCompanion }
import base.entity.auth.context.ChannelContext
import base.entity.error.model.ApiError
import base.entity.event.model.EventModel
import base.entity.kv.Key.Pipeline
import base.entity.question.model.{ AnswerModel, QuestionModel }

import scala.concurrent.Future

/**
 * CRUD, etc.
 * @author rconrad
 */
trait QuestionService extends Service {

  final val serviceManifest = manifest[QuestionService]

  def getQuestions(groupId: UUID,
                   userId: UUID)(implicit p: Pipeline,
                                 channelCtx: ChannelContext): Future[Either[ApiError, List[QuestionModel]]]

  def answer(input: AnswerModel)(implicit p: Pipeline,
                                 channelCtx: ChannelContext): Future[Either[ApiError, List[EventModel]]]

}

object QuestionService extends ServiceCompanion[QuestionService] {

}
