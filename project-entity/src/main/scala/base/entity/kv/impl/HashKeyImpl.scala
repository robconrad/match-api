/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 2:42 PM
 */

package base.entity.kv.impl

import java.nio.charset.Charset
import java.util.UUID

import base.common.lib.{ Ifo, Tryo }
import base.common.time.TimeService
import base.entity.kv.Key._
import base.entity.kv.KeyProps.{ CreatedProp, UpdatedProp }
import base.entity.kv.{ HashKey, Key, KeyProp }

import scala.collection.JavaConversions._
import scala.collection.{ breakOut, _ }
import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of ConcreteKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
// scalastyle:off null
abstract class HashKeyImpl extends KeyImpl with HashKey {

  def create() = setNx(CreatedProp, TimeService().asString())

  def getCreated = getDateTime(CreatedProp)
  def getUpdated = getDateTime(UpdatedProp)

  protected[impl] def getString(prop: Prop) =
    get_(prop).map(v => Ifo(v, v != null && v.length > 0))

  protected[impl] def getDateTime(prop: Prop) =
    get_(prop).map(v => Ifo(v, v != null).flatMap(v => Tryo(TimeService().fromString(v))))

  protected[impl] def getId(prop: Prop) =
    get_(prop).map(v => Ifo(v, v != null).flatMap(v => Tryo(UUID.fromString(v))))

  protected[impl] def getInt(prop: Prop) =
    get_(prop).map(v => Ifo(v, v != null).flatMap(v => Tryo(v.toInt)))

  protected[impl] def getLong(prop: Prop) =
    get_(prop).map(v => Ifo(v, v != null).flatMap(v => Tryo(Math.round(v.toDouble))))

  protected[impl] def getFlag(prop: Prop) =
    get_(prop).map(Key.string2Boolean)

  private def get_(prop: Prop) = p.hget(token, prop).map { v =>
    val res = v.asUTF8String()
    if (isDebugEnabled) log("HGET", s"prop: $prop, value: $res")
    res
  }

  protected[impl] def get = p.hgetall(token).map { v =>
    val res = v.asStringMap(Charset.defaultCharset()).toMap
    if (isDebugEnabled) log("HGETALL", s"props: $res")
    res.collect {
      case (k, v) if KeyProp(k).isDefined => (KeyProp(k).get, v)
    }.toMap
  }

  // note that an empty result is null for this
  protected[impl] def get(props: Array[Prop]) = {
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

  protected[impl] def getProps = p.hkeys(token).map { v =>
    val res = v.asStringSet(Charset.defaultCharset()).toList
    if (isDebugEnabled) log("HKEYS", s"value: $res")
    res.collect {
      case f if KeyProp(f).isDefined => KeyProp(f).get
    }
  }

  protected[impl] def setFlag(prop: Prop, value: Boolean) =
    set(prop, Key.boolean2Int(value))

  protected[impl] def set(prop: Prop, value: Any): Future[Boolean] =
    p.hset(token, prop, value).map { v =>
      val res = v.data().intValue()
      if (isDebugEnabled) log("HSET", s"prop: $prop, value: $value, result: $res")
      res >= 0
    }

  protected[impl] def set[T <: Map[Prop, Any]](props: T): Future[Boolean] =
    set_(props.map(_.productIterator.toList).flatten.toArray)

  private def set_(propValues: Array[Any]): Future[Boolean] = {
    assert(propValues.size > 1 && propValues.size % 2 == 0)
    val args = token +: propValues.map(_.asInstanceOf[AnyRef])
    p.hmset_(args: _*).map { v =>
      val res = v.data()
      if (isDebugEnabled) log("HMSET", "props: " + propValues.map(_.toString).toList + ", result: " + res)
      res == Key.STATUS_OK
    }
  }

  protected[impl] def setNx(prop: Prop, value: Any) = p.hsetnx(token, prop, value).map { v =>
    val res = v.data().intValue()
    if (isDebugEnabled) log("HSETNX", s"prop: $prop, value: $value, result: $res")
    res == 1
  }

  protected[impl] def increment(prop: Prop, value: Long) = p.hincrby(token, prop, value).map { v =>
    val res = Math.round(v.data().toDouble)
    if (isDebugEnabled) log("HINCRBY", s"prop: $prop, value: $value, result: $res")
    res
  }

  protected[impl] def del(prop: Prop) = {
    if (isDebugEnabled) log("HDEL", "prop: " + prop)
    p.hdel_(token, prop).map(_.data().intValue() == 1)
  }

  protected[impl] def del(props: List[Prop]) = {
    if (isDebugEnabled) log("HDEL", "props: " + props)
    val args = token +: props
    p.hdel_(args: _*).map(_.data().intValue() == props.size)
  }

}
