/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 10:25 PM
 */

package base.entity.question.kv.impl

import java.util.UUID

import base.common.time.TimeService
import base.entity.kv.Key.Prop
import base.entity.kv.KeyProps.CreatedProp
import base.entity.kv.impl.HashKeyImpl
import base.entity.question.QuestionDef
import base.entity.question.kv.QuestionKey
import base.entity.question.kv.QuestionKeyProps.{ CreatorIdProp, SideAProp, SideBProp }

/**
 * {{ Describe the high level purpose of QuestionKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class QuestionKeyImpl(val keyValue: UUID)
    extends HashKeyImpl[UUID]
    with QuestionKey {

  // scalastyle:off null
  def createDef(a: String, b: Option[String], userId: UUID) = {
    mSet(Map[Prop, Array[Byte]](
      SideAProp -> write(a),
      SideBProp -> b.map(write[String]).orNull,
      CreatorIdProp -> write(userId),
      CreatedProp -> write(TimeService().now)
    ).filter(_._2 != null))
  }

  def getQuestionDef(id: UUID) = mGetAsMap(SideAProp, SideBProp).map { props =>
    QuestionDef(id, read[String](props(SideAProp)), props.get(SideBProp).map(read[String]))
  }

  def getCreatorId = get(CreatorIdProp).map(_.map(read[UUID]))

}
