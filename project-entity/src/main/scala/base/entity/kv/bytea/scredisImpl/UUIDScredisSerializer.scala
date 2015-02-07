/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/7/15 3:40 PM
 */

package base.entity.kv.bytea.scredisImpl

import java.util.UUID

import base.entity.kv.bytea.ScredisSerializer
import scredis.serialization.{ UUIDWriter, UUIDReader }

/**
 * {{ Describe the high level purpose of UUIDScredisSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object UUIDScredisSerializer extends ScredisSerializer[UUID] {

  def writeImpl(value: UUID) = UUIDWriter.write(value)

  def readImpl(bytes: Array[Byte]) = UUIDReader.read(bytes)

}
