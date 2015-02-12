/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 9:56 PM
 */

package base.entity.kv.bytea.impl

import java.util.UUID

import base.entity.kv.bytea.Serializer
import scredis.serialization.{ UUIDWriter, UUIDReader }

/**
 * {{ Describe the high level purpose of UUIDScredisSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object UUIDSerializer extends Serializer[UUID] {

  def writeImpl(value: UUID) = UUIDWriter.write(value)

  def readImpl(bytes: Array[Byte]) = UUIDReader.read(bytes)

}
