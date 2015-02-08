/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/7/15 3:40 PM
 */

package base.entity.kv.bytea

import java.util.UUID

import base.entity.event.model.EventModel
import base.entity.facebook.FacebookInfo
import base.entity.kv.bytea.scredisImpl._
import base.entity.kv.{ OrderedIdPair, SortedIdPair }
import base.entity.question.QuestionIdComposite
import base.entity.user.kv.UserPhone
import scredis.serialization.{ Reader, Writer }

/**
 * {{ Describe the high level purpose of ByteaSerializers here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object ScredisSerializers {

  private val registry = Map[Manifest[_], ScredisSerializer[_]](
    manifest[EventModel] -> EventModelScredisSerializer,
    manifest[FacebookInfo] -> FacebookInfoScredisSerializer,
    manifest[SortedIdPair] -> IdPairScredisSerializer,
    manifest[OrderedIdPair] -> IdPairScredisSerializer,
    manifest[QuestionIdComposite] -> QuestionIdCompositeScredisSerializer,
    manifest[String] -> StringScredisSerializer,
    manifest[UserPhone] -> UserPhoneScredisSerializer,
    manifest[UUID] -> UUIDScredisSerializer
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

}