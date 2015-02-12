/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 5:29 PM
 */

package base.entity.kv.bytea.scredisImpl

import java.nio.ByteBuffer

import base.entity.kv.bytea.ScredisSerializer
import org.joda.time.DateTime

/**
 * {{ Describe the high level purpose of UUIDScredisSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object DateTimeScredisSerializer extends ScredisSerializer[DateTime] {

  def writeImpl(value: DateTime) = {
    val bb = ByteBuffer.wrap(new Array[Byte](8))
    bb.putLong(value.getMillis)
    bb.array()
  }

  def readImpl(bytes: Array[Byte]) = {
    val bb = ByteBuffer.wrap(bytes)
    new DateTime(bb.getLong)
  }

}
