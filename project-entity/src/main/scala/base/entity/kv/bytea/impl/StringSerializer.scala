/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 9:56 PM
 */

package base.entity.kv.bytea.impl

import base.entity.kv.bytea.Serializer
import scredis.serialization.{ UTF8StringReader, UTF8StringWriter }

/**
 * {{ Describe the high level purpose of StringScredisSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object StringSerializer extends Serializer[String] {

  def writeImpl(value: String) = UTF8StringWriter.write(value)

  def readImpl(bytes: Array[Byte]) = UTF8StringReader.read(bytes)

}
