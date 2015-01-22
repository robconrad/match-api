/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 9:27 AM
 */

package base.entity.kv

/**
 * {{ Describe the high level purpose of TypedKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait TypedKey[T] extends Key {

  protected def toType(data: Array[Byte]): T

  protected def fromType(data: T): Array[Byte]

}
