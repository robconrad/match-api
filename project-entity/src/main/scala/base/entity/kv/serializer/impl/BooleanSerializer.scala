/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:13 PM
 */

package base.entity.kv.serializer.impl

import base.entity.kv.serializer.Serializer
import scredis.exceptions.RedisReaderException

/**
 * {{ Describe the high level purpose of UUIDScredisSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object BooleanSerializer extends Serializer[Boolean] {

  val trueByte: Byte = 0x1
  val falseByte: Byte = 0x0

  val TRUE = Array[Byte](trueByte)
  val FALSE = Array[Byte](falseByte)

  val nonBooleanException = new RuntimeException("non-boolean result from scredis")

  def writeImpl(value: Boolean) = value match {
    case true  => TRUE
    case false => FALSE
  }

  def readImpl(bytes: Array[Byte]) = bytes(0) match {
    case b if b == trueByte  => true
    case b if b == falseByte => false
    case _                   => throw new RedisReaderException(nonBooleanException)
  }

}
