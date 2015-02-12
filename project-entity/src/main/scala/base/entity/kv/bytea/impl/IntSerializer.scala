/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 10:25 PM
 */

package base.entity.kv.bytea.impl

import base.entity.kv.bytea.Serializer
import scredis.serialization.{ IntReader, IntWriter }

/**
 * {{ Describe the high level purpose of StringScredisSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object IntSerializer extends Serializer[Int] {

  def writeImpl(value: Int) = IntWriter.write(value)

  def readImpl(bytes: Array[Byte]) = IntReader.read(bytes)

}
