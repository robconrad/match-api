/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 12:12 PM
 */

package base.entity.kv.serializer.impl

import java.nio.ByteBuffer

import base.entity.kv.serializer.Serializer

/**
 * {{ Describe the high level purpose of UUIDScredisSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object ShortSerializer extends Serializer[Short] {

  val shortLength = 2

  def writeImpl(value: Short) = {
    val bb = ByteBuffer.wrap(new Array[Byte](shortLength))
    bb.putShort(value)
    bb.array()
  }

  def readImpl(bytes: Array[Byte]) = {
    val bb = ByteBuffer.wrap(bytes)
    bb.getShort
  }

}
