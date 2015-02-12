/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 10:25 PM
 */

package base.entity.kv.impl

import base.common.lib.Dispatchable
import base.entity.kv.bytea.Serializers
import scredis.serialization.{ Reader, Writer }

trait KeyValueImpl[K, V] extends KeyImpl[K] with Dispatchable {

  implicit protected def mv: Manifest[V]

  implicit protected def valueWriter: Writer[V] = Serializers.writer[V]
  implicit protected def valueReader: Reader[V] = Serializers.reader[V]

}
