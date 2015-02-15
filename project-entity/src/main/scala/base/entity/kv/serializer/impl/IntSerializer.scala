/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 12:19 PM
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
object IntSerializer extends Serializer[Int] {

  val intLength = 4

  def writeImpl(value: Int) = {
    val bb = ByteBuffer.wrap(new Array[Byte](intLength))
    bb.putInt(value)
    bb.array()
  }

  def readImpl(bytes: Array[Byte]) = {
    val bb = ByteBuffer.wrap(bytes)
    bb.getInt
  }

}
