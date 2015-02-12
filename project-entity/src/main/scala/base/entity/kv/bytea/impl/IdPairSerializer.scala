/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 9:55 PM
 */

package base.entity.kv.bytea.impl

import base.entity.kv.bytea.Serializer
import base.entity.kv.{ IdPair, OrderedIdPair }
import scredis.serialization.{ UUIDReader, UUIDWriter }

/**
 * {{ Describe the high level purpose of StringByteaSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object IdPairSerializer extends Serializer[IdPair] {

  private val uuidLength = 16

  def writeImpl(v: IdPair) = {
    UUIDWriter.write(v.a) ++ UUIDWriter.write(v.b)
  }

  def readImpl(v: Array[Byte]) = OrderedIdPair(
    UUIDReader.read(v.slice(0, uuidLength)),
    UUIDReader.read(v.slice(uuidLength, 2 * uuidLength)))

}
