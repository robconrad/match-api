/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 5:18 PM
 */

package base.entity.kv.bytea.impl

import base.entity.kv.bytea.ByteaSerializer

/**
 * {{ Describe the high level purpose of StringByteaSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object StringByteaSerializer extends ByteaSerializer[String] {

  def serialize(v: String) = v.getBytes

  def deserialize(v: Array[Byte]) = new String(v)

}
