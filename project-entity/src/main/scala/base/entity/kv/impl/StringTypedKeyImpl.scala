/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 11:57 AM
 */

package base.entity.kv.impl

import base.entity.kv.TypedKey

/**
 * {{ Describe the high level purpose of StringTypedKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait StringTypedKeyImpl extends TypedKey[String] {

  protected def toType(data: Array[Byte]) = new String(data)

  protected def fromType(data: String) = data.getBytes

}
