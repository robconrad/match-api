/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/3/15 1:15 PM
 */

package base.entity.kv.impl

import java.nio.charset.Charset

import base.common.lib.{ Ifo, Tryo }
import base.entity.kv.Key.Pipeline
import base.entity.kv.{ HashKey, Key, KeyLogger }

import scala.collection.JavaConversions._
import scala.collection.breakOut
import scala.concurrent.Future

/**
 * Base class of a data model that relies on redis.
 *  Requires an id parameter that is a unique identifier of some kind
 */
// scalastyle:off null
private[kv] final class HashKeyImpl(val key: String, protected val logger: KeyLogger) extends KeyImpl with HashKey {

  def getString(prop: String)(implicit p: Pipeline) =
    get_(prop).map(v => Ifo(v, v != null && v.length > 0))

  def getInt(prop: String)(implicit p: Pipeline) =
    get_(prop).map(v => Ifo(v, v != null).flatMap(v => Tryo(v.toInt)))

  def getLong(prop: String)(implicit p: Pipeline) =
    get_(prop).map(v => Ifo(v, v != null).flatMap(v => Tryo(Math.round(v.toDouble))))

  def getFlag(prop: String)(implicit p: Pipeline) =
    get_(prop).map(Key.string2Boolean)

  private def get_(prop: String)(implicit p: Pipeline) = p.hget(key, prop).map { v =>
    val res = v.asAsciiString()
    if (isDebugEnabled) log("HGET", s"prop: $prop, value: $res")
    res
  }

  def get(implicit p: Pipeline) = p.hgetall(key).map { v =>
    val res = v.asStringMap(Charset.defaultCharset()).toMap
    if (isDebugEnabled) log("HGETALL", s"props: $res")
    res
  }

  // note that an empty result is null for this
  def get(props: Array[String])(implicit p: Pipeline) = {
    val args = key +: props.map(_.asInstanceOf[AnyRef])
    p.hmget_(args: _*).map { v =>
      val res = v.asStringList(Charset.defaultCharset()).toList
      val bag = props.zip(res)(breakOut).toMap.map {
        case (key: String, value) => value match {
          case null => (key, None)
          case _    => (key, Some(value))
        }
      }
      if (isDebugEnabled) log("HMGET", s"$bag")
      bag
    }
  }

  def getKeys(implicit p: Pipeline) = p.hkeys(key).map { v =>
    val res = v.asStringSet(Charset.defaultCharset()).toList
    if (isDebugEnabled) log("HKEYS", s"value: $res")
    res
  }

  def setFlag(prop: String, value: Boolean)(implicit p: Pipeline) =
    set(prop, Key.boolean2Int(value))

  def set(prop: String, value: Any)(implicit p: Pipeline): Future[Boolean] = {
    if (isDebugEnabled) log("HSET", s"prop: $prop, value: $value")
    p.hset(key, prop, value).map(_.data().intValue() >= 0)
  }

  def set(props: Map[String, Any])(implicit p: Pipeline): Future[Boolean] =
    set_(props.map(_.productIterator.toList).flatten.toArray)

  private def set_(propValues: Array[Any])(implicit p: Pipeline): Future[Boolean] = {
    if (isDebugEnabled) log("HMSET", "props: " + propValues.map(_.toString).toList)
    assert(propValues.size > 1 && propValues.size % 2 == 0)
    val args = key +: propValues.map(_.asInstanceOf[AnyRef])
    p.hmset_(args: _*).map(_.data() == Key.STATUS_OK)
  }

  def setNx(prop: String, value: Any)(implicit p: Pipeline) = {
    if (isDebugEnabled) log("HSETNX", "prop: " + prop + " value: " + value.toString)
    p.hsetnx(key, prop, value).map(_.data().intValue() == 1)
  }

  def increment(prop: String, value: Long)(implicit p: Pipeline) = p.hincrby(key, prop, value).map { v =>
    val res = Math.round(v.data().toDouble)
    if (isDebugEnabled) log("HINCRBY", s"prop: $prop, value: $value, result: $res")
    res
  }

  def del(prop: String)(implicit p: Pipeline) = {
    if (isDebugEnabled) log("HDEL", "prop: " + prop)
    p.hdel_(key, prop).map(_.data().intValue() == 1)
  }

  def del(props: List[String])(implicit p: Pipeline) = {
    if (isDebugEnabled) log("HDEL", "props: " + props)
    p.hdel(key, props.toArray).map(_.data().intValue() == props.size)
  }

}

