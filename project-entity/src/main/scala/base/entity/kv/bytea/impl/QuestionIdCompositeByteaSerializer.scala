/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 5:18 PM
 */

package base.entity.kv.bytea.impl

import base.entity.kv.bytea.ByteaSerializer
import base.entity.question.{ QuestionIdComposite, QuestionSides }
import redis.client.RedisException

/**
 * {{ Describe the high level purpose of QuestionIdCompositeByteaSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
// scalastyle:off null
object QuestionIdCompositeByteaSerializer extends ByteaSerializer[QuestionIdComposite] {

  val compositeIdLength = 17
  val uuidLength = 16

  def serialize(data: QuestionIdComposite) = {
    val side = data.side.toString.getBytes
    assert(side.length == 1)
    UuidUtil.fromUuid(data.questionId) ++ side
  }

  def deserialize(data: Array[Byte]) = {
    data match {
      case null =>
        throw new RedisException("toType received null")
      case data if data.length != compositeIdLength =>
        throw new RedisException(s"toType received invalid QuestionIdComposite $data")
      case data =>
        val uuid = UuidUtil.toUuid(data.slice(0, uuidLength)).getOrElse {
          throw new RedisException(s"toType received invalid uuid $data")
        }
        val side = QuestionSides.withName(new String(data.slice(uuidLength, compositeIdLength)))
        QuestionIdComposite(uuid, side)
    }
  }

}
