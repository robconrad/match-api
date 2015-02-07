/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/7/15 3:40 PM
 */

package base.entity.kv.bytea.scredisImpl

import base.entity.kv.bytea.ScredisSerializer
import base.entity.kv.{ IdPair, OrderedIdPair }
import scredis.serialization.{ UUIDReader, UUIDWriter }

/**
 * {{ Describe the high level purpose of StringByteaSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object IdPairScredisSerializer extends ScredisSerializer[IdPair] {

  private val uuidLength = 16

  def writeImpl(v: IdPair) = {
    UUIDWriter.write(v.a) ++ UUIDWriter.write(v.b)
  }

  def readImpl(v: Array[Byte]) = OrderedIdPair(
    UUIDReader.read(v.slice(0, uuidLength)),
    UUIDReader.read(v.slice(uuidLength, 2 * uuidLength)))

}
