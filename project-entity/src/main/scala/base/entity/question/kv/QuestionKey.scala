/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 9:05 PM
 */

package base.entity.question.kv

import java.util.UUID

import base.entity.kv.HashKey
import base.entity.question.QuestionDef

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of QuestionKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait QuestionKey extends HashKey {

  def createDef(a: String, b: Option[String], userId: UUID): Future[Boolean]

  def getQuestionDef(id: UUID): Future[QuestionDef]

  def getCreatorId: Future[Option[UUID]]

}
