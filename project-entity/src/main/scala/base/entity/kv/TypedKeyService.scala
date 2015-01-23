/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 3:38 PM
 */

package base.entity.kv

/**
 * {{ Describe the high level purpose of TypedKeyService here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait TypedKeyService[T] {

  protected def toBytes(id: T): Array[Byte]
  protected def fromBytes(id: Array[Byte]): T

}
