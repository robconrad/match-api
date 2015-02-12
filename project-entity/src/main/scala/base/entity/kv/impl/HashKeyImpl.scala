/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 7:30 PM
 */

package base.entity.kv.impl

import base.entity.kv.Key._
import base.entity.kv.bytea.ScredisSerializers
import base.entity.kv.{HashKey, KeyProp, ScredisFactoryService}
import scredis.serialization.{BytesReader, Reader}

abstract class HashKeyImpl[K](implicit val mk: Manifest[K])
    extends ScredisKeyImpl[K]
    with HashKey[K] {

  private lazy val commands = ScredisFactoryService().hashCommands

  protected implicit def valueReader: Reader[Array[Byte]] = BytesReader

  protected implicit def prop2String(p: Prop): String = p.toString
  protected implicit def props2String(p: Seq[Prop]): Seq[String] = p.map(_.toString)
  protected implicit def propMap2StringMap[T](p: Map[Prop, T]): Map[String, T] = p.map { case (k,v) =>
    k.toString -> v
  }

  protected def read[T](v: Array[Byte])(implicit m: Manifest[T]) = ScredisSerializers.read(v)(m)
  protected def write[T](v: T)(implicit m: Manifest[T]) = ScredisSerializers.write(v)(m)

  protected def keyCommands = commands

  protected def del(fields: Prop*) =
    commands.hDel(key, fields: _*)

  protected def exists(field: Prop) =
    commands.hExists(key, field)

  protected def get(field: Prop) =
    commands.hGet(key, field)

  protected def getAll =
    commands.hGetAll(key).map(_.map(_.map {
      case (prop, value) => KeyProp(prop).orNull -> value
    }.toMap))

  protected def incrBy(field: Prop, count: Long) =
    commands.hIncrBy(key, field, count)

  protected def incrByFloat(field: Prop, count: Double) =
    commands.hIncrByFloat(key, field, count)

  protected def keys =
    commands.hKeys(key).map(_.map(KeyProp(_).orNull))

  protected def len =
    commands.hLen(key)

  protected def mGet(fields: Prop*) =
    commands.hmGet(key, fields: _*)

  protected def mGetAsMap(fields: Prop*) =
    commands.hmGetAsMap(key, fields:_*)

  protected def mSet(fieldValuePairs: Map[Prop, Array[Byte]]) =
    commands.hmSet(key, fieldValuePairs)

  protected def scan(cursor: Long, matchOpt: Option[String], countOpt: Option[Int]) =
    commands.hScan(key, cursor, matchOpt, countOpt).map {
      case (cursor, fields) => cursor -> fields.map {
        case (field, reader) => (KeyProp(field).orNull, reader)
      }
    }

  protected def set(field: Prop, value: Array[Byte]) =
    commands.hSet(key, field, value)

  protected def setNX(field: Prop, value: Array[Byte]) =
    commands.hSetNX(key, field, value)

  protected def vals =
    commands.hVals(key)

}
