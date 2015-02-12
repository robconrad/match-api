/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 7:52 PM
 */

package base.entity.kv.bytea.scredisImpl

import base.entity.kv.bytea.ScredisSerializer
import scredis.serialization.{LongReader, LongWriter}

/**
 * {{ Describe the high level purpose of StringScredisSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object LongScredisSerializer extends ScredisSerializer[Long] {

  def writeImpl(value: Long) = LongWriter.write(value)

  def readImpl(bytes: Array[Byte]) = LongReader.read(bytes)

}
