/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 6:24 PM
 */

package base.entity.kv.bytea.scredisImpl

import base.entity.kv.bytea.ScredisSerializer
import scredis.serialization.{IntReader, IntWriter}

/**
 * {{ Describe the high level purpose of StringScredisSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object IntScredisSerializer extends ScredisSerializer[Int] {

  def writeImpl(value: Int) = IntWriter.write(value)

  def readImpl(bytes: Array[Byte]) = IntReader.read(bytes)

}
