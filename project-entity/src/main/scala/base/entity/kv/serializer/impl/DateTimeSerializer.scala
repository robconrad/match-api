/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/12/15 8:46 PM
 */

package base.entity.kv.serializer.impl

import java.nio.ByteBuffer

import base.entity.kv.serializer.Serializer
import org.joda.time.DateTime

/**
 * {{ Describe the high level purpose of UUIDScredisSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object DateTimeSerializer extends Serializer[DateTime] {

  val longLength = 8

  def writeImpl(value: DateTime) = {
    val bb = ByteBuffer.wrap(new Array[Byte](longLength))
    bb.putLong(value.getMillis)
    bb.array()
  }

  def readImpl(bytes: Array[Byte]) = {
    val bb = ByteBuffer.wrap(bytes)
    new DateTime(bb.getLong)
  }

}
