/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 7:20 PM
 */

package base.entity.kv.bytea.scredisImpl

import base.entity.kv.bytea.ScredisSerializer
import scredis.serialization.{BooleanReader, BooleanWriter}

/**
 * {{ Describe the high level purpose of UUIDScredisSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object BooleanScredisSerializer extends ScredisSerializer[Boolean] {

  def writeImpl(value: Boolean) = BooleanWriter.write(value)

  def readImpl(bytes: Array[Byte]) = BooleanReader.read(bytes)

}