/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 4:10 PM
 */

package base.entity.kv.impl

import base.entity.kv.{ IdPair, TypedKeyService }

/**
 * {{ Describe the high level purpose of StringTypedKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait IdPairTypedKeyServiceImpl extends TypedKeyService[IdPair] {

  private val uuidLength = 16

  def toBytes(id: IdPair) = UuidUtil.fromUuid(id.a) ++ UuidUtil.fromUuid(id.b)

  def fromBytes(id: Array[Byte]) =
    IdPair(UuidUtil.toUuid(id.slice(0, uuidLength)).get, UuidUtil.toUuid(id.slice(uuidLength, 2 * uuidLength)).get)

}
