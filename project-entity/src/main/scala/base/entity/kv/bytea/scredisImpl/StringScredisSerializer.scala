/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/7/15 3:40 PM
 */

package base.entity.kv.bytea.scredisImpl

import base.entity.kv.bytea.ScredisSerializer
import scredis.serialization.{ UTF8StringReader, UTF8StringWriter }

/**
 * {{ Describe the high level purpose of StringScredisSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object StringScredisSerializer extends ScredisSerializer[String] {

  def writeImpl(value: String) = UTF8StringWriter.write(value)

  def readImpl(bytes: Array[Byte]) = UTF8StringReader.read(bytes)

}
