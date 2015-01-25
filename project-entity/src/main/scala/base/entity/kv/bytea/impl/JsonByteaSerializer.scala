/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 11:23 AM
 */

package base.entity.kv.bytea.impl

import base.entity.json.JsonFormats
import base.entity.kv.bytea.ByteaSerializer
import org.json4s.native.{ JsonMethods, Serialization }

/**
 * {{ Describe the high level purpose of JsonByteaSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait JsonByteaSerializer[T <: AnyRef] extends ByteaSerializer[T] {

  implicit val formats = JsonFormats.withModels

  implicit val m: Manifest[T]

  def serialize(v: T) = Serialization.write[T](v).getBytes

  def deserialize(v: Array[Byte]) = JsonMethods.parse(new String(v)).extract[T]

}
