/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 9:24 PM
 */

package base.entity.question.kv.impl

import java.util.UUID

import base.common.time.TimeService
import base.entity.kv.Key._
import base.entity.kv.KeyLogger
import base.entity.kv.KeyProps.CreatedProp
import base.entity.kv.impl.HashKeyImpl
import base.entity.question.QuestionDef
import base.entity.question.kv.QuestionKey
import base.entity.question.kv.QuestionKeyProps.{ SideBProp, SideAProp, CreatorIdProp }

/**
 * {{ Describe the high level purpose of QuestionKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class QuestionKeyImpl(val token: Array[Byte],
                      protected val logger: KeyLogger)(implicit protected val p: Pipeline)
    extends HashKeyImpl with QuestionKey {

  def createDef(a: String, b: Option[String], userId: UUID) = {
    set(Map[Prop, Any](
      SideAProp -> a,
      SideBProp -> b.getOrElse(""),
      CreatorIdProp -> userId,
      CreatedProp -> TimeService().asString())
      .filter(_._2 != ""))
  }

  val getQuestionDefProps = Array[Prop](SideAProp, SideBProp)
  def getQuestionDef(id: UUID) = get(getQuestionDefProps).map { props =>
    QuestionDef(id, props(SideAProp).get, props(SideBProp))
  }

  def getCreatorId = getId(CreatorIdProp)

}
