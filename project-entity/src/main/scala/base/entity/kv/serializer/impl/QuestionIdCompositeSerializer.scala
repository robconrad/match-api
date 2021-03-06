/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/12/15 8:46 PM
 */

package base.entity.kv.serializer.impl

import base.entity.kv.serializer.Serializer
import base.entity.question.{ QuestionIdComposite, QuestionSides }
import scredis.exceptions.RedisException
import scredis.serialization.{ UUIDReader, UUIDWriter }

/**
 * {{ Describe the high level purpose of QuestionIdCompositeByteaSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
// scalastyle:off null
object QuestionIdCompositeSerializer extends Serializer[QuestionIdComposite] {

  val compositeIdLength = 17
  val uuidLength = 16

  def writeImpl(data: QuestionIdComposite) = {
    val side = data.side.toString.getBytes
    assert(side.length == 1)
    UUIDWriter.write(data.questionId) ++ side
  }

  def readImpl(data: Array[Byte]) = {
    data match {
      case null =>
        throw new RedisException("toType received null") {}
      case data if data.length != compositeIdLength =>
        throw new RedisException(s"toType received invalid QuestionIdComposite length ${data.length} $data") {}
      case data =>
        val uuid = UUIDReader.read(data.slice(0, uuidLength))
        val side = QuestionSides.withName(new String(data.slice(uuidLength, compositeIdLength)))
        QuestionIdComposite(uuid, side)
    }
  }

}
