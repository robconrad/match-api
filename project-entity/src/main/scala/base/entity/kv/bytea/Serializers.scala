/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 9:56 PM
 */

package base.entity.kv.bytea

import java.util.UUID

import base.entity.event.model.EventModel
import base.entity.facebook.FacebookInfo
import base.entity.kv.bytea.impl._
import base.entity.kv.{ OrderedIdPair, SortedIdPair }
import base.entity.question.QuestionIdComposite
import base.entity.user.kv.UserPhone
import org.joda.time.DateTime
import scredis.serialization.{ Reader, Writer }

/**
 * {{ Describe the high level purpose of ByteaSerializers here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object Serializers {

  private val registry = Map[Manifest[_], Serializer[_]](
    manifest[Boolean] -> BooleanSerializer,
    manifest[DateTime] -> DateTimeSerializer,
    manifest[EventModel] -> EventModelSerializer,
    manifest[FacebookInfo] -> FacebookInfoSerializer,
    manifest[Int] -> IntSerializer,
    manifest[Long] -> LongSerializer,
    manifest[OrderedIdPair] -> IdPairSerializer,
    manifest[QuestionIdComposite] -> QuestionIdCompositeSerializer,
    manifest[SortedIdPair] -> IdPairSerializer,
    manifest[String] -> StringSerializer,
    manifest[UserPhone] -> UserPhoneSerializer,
    manifest[UUID] -> UUIDSerializer
  )

  def reader[T](implicit m: Manifest[T]): Reader[T] = {
    registry.get(m) match {
      case Some(reader) => reader.asInstanceOf[Reader[T]]
      case _            => throw new RuntimeException(s"reader not defined for $m")
    }
  }

  def writer[T](implicit m: Manifest[T]): Writer[T] = {
    registry.get(m) match {
      case Some(writer) => writer.asInstanceOf[Writer[T]]
      case _            => throw new RuntimeException(s"writer not defined for $m")
    }
  }

  def read[T](v: Array[Byte])(implicit m: Manifest[T]): T = reader[T].read(v)

  def write[T](v: T)(implicit m: Manifest[T]): Array[Byte] = writer[T].write(v)

}
