/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 5:47 PM
 */

package base.entity.kv.bytea.impl

import base.entity.kv.{ SortedIdPair, OrderedIdPair, IdPair }
import base.entity.kv.bytea.ByteaSerializer

/**
 * {{ Describe the high level purpose of StringByteaSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object IdPairByteaSerializer extends ByteaSerializer[IdPair] {

  private val uuidLength = 16

  def serialize(v: IdPair) = {
    UuidUtil.fromUuid(v.a) ++ UuidUtil.fromUuid(v.b)
  }

  def deserialize(v: Array[Byte]) = OrderedIdPair(
    UuidUtil.toUuid(v.slice(0, uuidLength)).get,
    UuidUtil.toUuid(v.slice(uuidLength, 2 * uuidLength)).get)

}
