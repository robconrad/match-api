/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 5:18 PM
 */

package base.entity.kv.bytea

/**
 * {{ Describe the high level purpose of ByteaSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait ByteaSerializer[T] {

  def serialize(v: T): Array[Byte]

  def deserialize(v: Array[Byte]): T

}
