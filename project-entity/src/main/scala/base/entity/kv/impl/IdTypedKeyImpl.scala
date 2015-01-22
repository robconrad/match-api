/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 10:45 AM
 */

package base.entity.kv.impl

import java.util.UUID

import base.common.logging.Loggable
import base.entity.kv.TypedKey
import redis.client.RedisException

/**
 * {{ Describe the high level purpose of IdTypedKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
// scalastyle:off null
trait IdTypedKeyImpl extends TypedKey[UUID] with Loggable {

  protected def toType(data: Array[Byte]) = {
    data match {
      case null =>
        throw new RedisException("toType received null")
      case data if !UuidUtil.isValid(data) =>
        throw new RedisException(s"toType received invalid uuid $data")
      case data =>
        UuidUtil.toUuid(data).getOrElse(throw new RedisException(s"toType received invalid uuid $data"))
    }
  }

  protected def fromType(data: UUID) = {
    UuidUtil.fromUuid(data)
  }

}
