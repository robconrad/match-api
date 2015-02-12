/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/10/15 8:43 PM
 */

package base.entity.kv.impl

import base.common.lib.Dispatchable
import base.entity.kv.bytea.ScredisSerializers
import scredis.serialization.{Reader, Writer}

trait ScredisKeyValueImpl[K, V] extends ScredisKeyImpl[K] with Dispatchable {

  implicit protected def mv: Manifest[V]

  implicit protected def valueWriter: Writer[V] = ScredisSerializers.writer[V]
  implicit protected def valueReader: Reader[V] = ScredisSerializers.reader[V]

}
