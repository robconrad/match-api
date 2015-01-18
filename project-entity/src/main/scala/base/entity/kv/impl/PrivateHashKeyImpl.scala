/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:28 PM
 */

package base.entity.kv.impl

import java.nio.charset.Charset
import java.util.UUID

import base.common.lib.{ Ifo, Tryo }
import base.common.time.TimeService
import base.entity.kv.Key.{ Pipeline, Prop }
import base.entity.kv.{ Key, KeyLogger, KeyProp, PrivateHashKey }

import scala.collection.JavaConversions._
import scala.collection.breakOut
import scala.concurrent.Future

/**
 * Base class of a data model that relies on redis.
 *  Requires an id parameter that is a unique identifier of some kind
 */
// scalastyle:off null
final class PrivateHashKeyImpl(val token: String, protected val logger: KeyLogger)(implicit protected val p: Pipeline)
    extends KeyImpl with PrivateHashKey {

  def getString(prop: Prop) =
    get_(prop).map(v => Ifo(v, v != null && v.length > 0))

  def getDateTime(prop: Prop) =
    get_(prop).map(v => Ifo(v, v != null).flatMap(v => Tryo(TimeService().fromString(v))))

  def getId(prop: Prop) =
    get_(prop).map(v => Ifo(v, v != null).flatMap(v => Tryo(UUID.fromString(v))))

  def getInt(prop: Prop) =
    get_(prop).map(v => Ifo(v, v != null).flatMap(v => Tryo(v.toInt)))

  def getLong(prop: Prop) =
    get_(prop).map(v => Ifo(v, v != null).flatMap(v => Tryo(Math.round(v.toDouble))))

  def getFlag(prop: Prop) =
    get_(prop).map(Key.string2Boolean)

  private def get_(prop: Prop) = p.hget(token, prop).map { v =>
    val res = v.asUTF8String()
    if (isDebugEnabled) log("HGET", s"prop: $prop, value: $res")
    res
  }

  def get = p.hgetall(token).map { v =>
    val res = v.asStringMap(Charset.defaultCharset()).toMap
    if (isDebugEnabled) log("HGETALL", s"props: $res")
    res.collect {
      case (k, v) if KeyProp(k).isDefined => (KeyProp(k).get, v)
    }.toMap
  }

  // note that an empty result is null for this
  def get(props: Array[Prop]) = {
    val args = token +: props.map(_.asInstanceOf[AnyRef])
    p.hmget_(args: _*).map { v =>
      val res = v.asStringList(Charset.defaultCharset()).toList
      val bag = props.zip(res)(breakOut).map {
        case (key, value) => key -> Option(value)
      }.toMap[Prop, Option[String]]
      if (isDebugEnabled) log("HMGET", s"$bag")
      bag
    }
  }

  def getProps = p.hkeys(token).map { v =>
    val res = v.asStringSet(Charset.defaultCharset()).toList
    if (isDebugEnabled) log("HKEYS", s"value: $res")
    res.collect {
      case f if KeyProp(f).isDefined => KeyProp(f).get
    }
  }

  def setFlag(prop: Prop, value: Boolean) =
    set(prop, Key.boolean2Int(value))

  def set(prop: Prop, value: Any): Future[Boolean] = {
    if (isDebugEnabled) log("HSET", s"prop: $prop, value: $value")
    p.hset(token, prop, value).map(_.data().intValue() >= 0)
  }

  def set(props: Map[Prop, Any]): Future[Boolean] =
    set_(props.map(_.productIterator.toList).flatten.toArray)

  private def set_(propValues: Array[Any]): Future[Boolean] = {
    if (isDebugEnabled) log("HMSET", "props: " + propValues.map(_.toString).toList)
    assert(propValues.size > 1 && propValues.size % 2 == 0)
    val args = token +: propValues.map(_.asInstanceOf[AnyRef])
    p.hmset_(args: _*).map(_.data() == Key.STATUS_OK)
  }

  def setNx(prop: Prop, value: Any) = {
    if (isDebugEnabled) log("HSETNX", "prop: " + prop + " value: " + value.toString)
    p.hsetnx(token, prop, value).map(_.data().intValue() == 1)
  }

  def increment(prop: Prop, value: Long) = p.hincrby(token, prop, value).map { v =>
    val res = Math.round(v.data().toDouble)
    if (isDebugEnabled) log("HINCRBY", s"prop: $prop, value: $value, result: $res")
    res
  }

  def del(prop: Prop) = {
    if (isDebugEnabled) log("HDEL", "prop: " + prop)
    p.hdel_(token, prop).map(_.data().intValue() == 1)
  }

  def del(props: List[Prop]) = {
    if (isDebugEnabled) log("HDEL", "props: " + props)
    val args = token +: props
    p.hdel_(args: _*).map(_.data().intValue() == props.size)
  }

}

