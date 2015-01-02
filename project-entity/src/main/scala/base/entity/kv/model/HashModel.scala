/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/1/15 8:04 PM
 */

package base.entity.kv.model

import java.nio.charset.Charset

import base.common.lib.{ Ifo, Tryo }

import scala.collection.JavaConversions._
import scala.collection.breakOut
import scala.concurrent.Future

/**
 * Base class of a data model that relies on redis.
 *  Requires an id parameter that is a unique identifier of some kind
 * @param _OBJECT static extension of ObjectModel specific to the data model
 * @param id unique identifier
 */
// scalastyle:off null
abstract class HashModel(_OBJECT: HashObject, id: String) extends RedisModel(_OBJECT, id) {

  protected def getString(prop: String) = get(prop).map(v => Ifo(v, v != null && v.length > 0))
  protected def getInt(prop: String) = get(prop).map(v => Ifo(v, v != null).flatMap(v => Tryo(v.toInt)))
  protected def getLong(prop: String) = get(prop).map(v => Ifo(v, v != null).flatMap(v => Tryo(Math.round(v.toDouble))))
  protected def getFlag(prop: String) = get(prop).map(_OBJECT.string2Boolean)

  protected def get(prop: String) = pipeline.hget(key, prop).map { v =>
    val res = v.asAsciiString()
    if (isDebugEnabled) log("HGET", s"prop: $prop, value: $res")
    res
  }

  protected def getAll = pipeline.hgetall(key).map { v =>
    val res = v.asStringMap(Charset.defaultCharset()).toMap
    if (isDebugEnabled) log("HGETALL", s"props: $res")
    res
  }

  protected def incrby(prop: String, value: Long) = pipeline.hincrby(key, prop, value).map { v =>
    val res = Math.round(v.data().toDouble)
    if (isDebugEnabled) log("HINCRBY", s"prop: $prop, value: $value, result: $res")
    res
  }

  // note that an empty result is null for this
  protected def mGet(props: Array[String]) = {
    val args = key +: props.map(_.asInstanceOf[AnyRef])
    pipeline.hmget_(args: _*).map { v =>
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

  protected def getKeys = pipeline.hkeys(key).map { v =>
    val res = v.asStringSet(Charset.defaultCharset()).toList
    if (isDebugEnabled) log("HKEYS", s"value: $res")
    res
  }

  protected def setFlag(prop: String, value: Boolean) =
    set(prop, _OBJECT.boolean2Int(value))

  protected def set(prop: String, value: Any): Future[Boolean] = {
    if (isDebugEnabled) log("HSET", s"prop: $prop, value: $value")
    pipeline.hset(key, prop, value).map(_.data().intValue() >= 0)
  }

  protected def set(props: Map[String, Any]): Future[Boolean] =
    set(props.map(_.productIterator.toList).flatten.toArray)

  private def set(propValues: Array[Any]): Future[Boolean] = {
    if (isDebugEnabled) log("HMSET", "props: " + propValues.map(_.toString).toList)
    assert(propValues.size > 1 && propValues.size % 2 == 0)
    val args = key +: propValues.map(_.asInstanceOf[AnyRef])
    pipeline.hmset_(args: _*).map(_.data() == _OBJECT.STATUS_OK)
  }

  protected def setNx(prop: String, value: Any) = {
    if (isDebugEnabled) log("HSETNX", "prop: " + prop + " value: " + value.toString)
    pipeline.hsetnx(key, prop, value).map(_.data().intValue() == 1)
  }

  protected def del(prop: String) = {
    if (isDebugEnabled) log("HDEL", "prop: " + prop)
    pipeline.hdel_(key, prop).map(_.data().intValue() == 1)
  }

  protected def del(props: List[String]) = {
    if (isDebugEnabled) log("HDEL", "props: " + props)
    pipeline.hdel(key, props.toArray).map(_.data().intValue() == props.size)
  }

}

abstract class HashObject extends RedisObject {

  protected def getMulti(prop: String, ids: Iterable[String]): Future[Map[String, Option[String]]] = {
    if (isDebugEnabled) log("HGET-MULTI", s"prop: $prop, ids: $ids")
    ids.size > 0 match {
      case true =>
        val futures = ids.map(id => {
          if (isDebugEnabled) log("HGET", getKey(id), s"prop: $prop")
          guavaFutureToAkka(pipeline.hget(getKey(id), prop))
        }).toList // has to be list or gets interpreted as set later, killing duplicate results
        Future.sequence(futures).map { v =>
          ids.zip(v.map(_.asAsciiString())).toMap.map {
            case (key, value) => value match {
              case null => (key, None)
              case _    => (key, Some(value))
            }
          }
        }
      case false => Future.successful(Map())
    }
  }

  protected def mGetMulti(props: Array[String],
                          ids: Iterable[String]): Future[Map[String, Map[String, Option[String]]]] = {
    if (isDebugEnabled) log("HMGET-MULTI (start)", "props: " + props.map(_.toString).toList + s" ids: $ids")
    ids.size > 0 match {
      case true =>
        val futures = ids.map(id => {
          if (isDebugEnabled) log("HMGET", getKey(id), "props: " + props.map(_.toString).toList)
          val args = getKey(id) +: props.map(_.asInstanceOf[AnyRef])
          guavaFutureToAkka(pipeline.hmget_(args: _*))
        }).toList // has to be list or gets interpreted as set later, killing duplicate results
        Future.sequence(futures).map { v =>
          val res = v.map { reply =>
            val res = reply.asStringList(Charset.defaultCharset()).toList
            (props zip res)(breakOut).toMap.map {
              case (key, value) => value match {
                case null => (key, None)
                case _    => (key, Some(value))
              }
            }
          }
          val ret = (ids zip res).toMap
          if (isDebugEnabled) {
            log("HMGET-MULTI (finish)", "props: " + props.map(_.toString).toList + s" ids: $ids res: $ret")
          }
          ret
        }
      case false => Future.successful(Map())
    }
  }

  protected def incrbyMulti(prop: String, values: Map[String, Long]): Future[Map[String, Long]] = {
    if (isDebugEnabled) log("HINCRBY-MULTI", s"prop: $prop, values: $values")
    val keys = values.keys.toList

    keys.length > 0 match {
      case true =>
        val futures = keys.map { id =>
          if (isDebugEnabled) log("HINCRBY", getKey(id), s"prop: $prop, value: ${values(id)}")
          guavaFutureToAkka(pipeline.hincrby(getKey(id), prop, values(id))).map { v =>
            id -> Math.round(v.data().toDouble)
          }
        }.toList // has to be list or gets interpreted as set later, killing duplicate results
        Future.sequence(futures).map(_.toMap)
      case false => Future.successful(Map())
    }
  }

}
