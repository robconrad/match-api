/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 9:54 PM
 */

package base.entity.kv.bytea.impl

import base.entity.json.JsonFormats
import base.entity.kv.bytea.Serializer
import org.json4s.native.{ JsonMethods, Serialization }

/**
 * {{ Describe the high level purpose of JsonByteaSerializer here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait JsonScredisSerializer[T <: AnyRef] extends Serializer[T] {

  implicit val formats = JsonFormats.withModels

  implicit val m: Manifest[T]

  def writeImpl(v: T) = Serialization.write[T](v).getBytes

  def readImpl(v: Array[Byte]) = JsonMethods.parse(new String(v)).extract[T]

}
