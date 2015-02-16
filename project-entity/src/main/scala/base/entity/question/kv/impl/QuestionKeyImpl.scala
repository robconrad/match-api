/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:13 PM
 */

package base.entity.question.kv.impl

import java.util.UUID

import base.common.lib.Dispatchable
import base.common.time.TimeService
import base.entity.kv.serializer.SerializerImplicits._
import base.entity.question.QuestionDef
import base.entity.question.kv.QuestionKey
import base.entity.question.kv.impl.QuestionKeyImpl._
import scredis.exceptions.RedisException
import scredis.keys.{ HashKey, HashKeyProp, HashKeyProps }
import scredis.serialization.{ UTF8StringReader, UTF8StringWriter }

/**
 * {{ Describe the high level purpose of QuestionKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class QuestionKeyImpl(keyFactory: HashKeyProps => HashKey[Short, UUID])
    extends QuestionKey
    with Dispatchable {

  private lazy val key = keyFactory(props)

  // scalastyle:off null
  def createDef(a: String, b: Option[String], userId: UUID) = {
    key.mSet(Map[HashKeyProp, Array[Byte]](
      SideAProp -> UTF8StringWriter.write(a),
      SideBProp -> b.map(UTF8StringWriter.write).orNull,
      CreatorIdProp -> uuidWriter.write(userId),
      CreatedProp -> dateTimeSerializer.write(TimeService().now)
    ).filter(_._2 != null))
  }

  def getQuestionDef(id: UUID) = key.mGetAsMap[Array[Byte]](SideAProp, SideBProp).map { props =>
    if (!props.isDefinedAt(SideAProp)) {
      throw new RedisException() {}
    }
    QuestionDef(id, UTF8StringReader.read(props(SideAProp)), props.get(SideBProp).map(UTF8StringReader.read))
  }

  def getCreatorId = key.get[UUID](CreatorIdProp)

}

object QuestionKeyImpl {

  val CreatedProp = HashKeyProp("created")
  val SideAProp = HashKeyProp("a")
  val SideBProp = HashKeyProp("b")
  val CreatorIdProp = HashKeyProp("creator")

  val props = HashKeyProps(Set(
    CreatedProp,
    SideAProp,
    SideBProp,
    CreatorIdProp))

}
