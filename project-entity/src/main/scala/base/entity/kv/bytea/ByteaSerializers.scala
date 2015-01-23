/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 5:18 PM
 */

package base.entity.kv.bytea

import java.util.UUID

import base.entity.event.model.EventModel
import base.entity.kv.IdPair
import base.entity.kv.bytea.impl._
import base.entity.question.QuestionIdComposite

/**
 * {{ Describe the high level purpose of ByteaSerializers here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object ByteaSerializers {

  private val registry = Map[Manifest[_], ByteaSerializer[_]](
    manifest[String] -> StringByteaSerializer,
    manifest[UUID] -> IdByteaSerializer,
    manifest[QuestionIdComposite] -> QuestionIdCompositeByteaSerializer,
    manifest[IdPair] -> IdPairByteaSerializer,
    manifest[EventModel] -> EventModelByteaSerializer
  )

  def serialize[T](v: T)(implicit m: Manifest[T]) = {
    registry.get(m) match {
      case Some(serializer) => serializer.asInstanceOf[ByteaSerializer[T]].serialize(v)
      case _                => throw new RuntimeException(s"serializer not defined for $m")
    }
  }

  def deserialize[T](v: Array[Byte])(implicit m: Manifest[T]): T = {
    registry.get(m) match {
      case Some(serializer) => serializer.asInstanceOf[ByteaSerializer[T]].deserialize(v)
      case _                => throw new RuntimeException(s"serializer not defined for $m")
    }
  }

}
