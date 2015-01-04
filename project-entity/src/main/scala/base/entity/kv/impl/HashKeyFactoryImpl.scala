/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/3/15 1:15 PM
 */

package base.entity.kv.impl

import java.nio.charset.Charset

import base.entity.kv.Key._
import base.entity.kv.{ HashKeyFactory, KvService }

import scala.collection.JavaConversions._
import scala.collection.breakOut
import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of HashFactory here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
// scalastyle:off null
private[kv] final class HashKeyFactoryImpl(protected val keyChannel: KeyChannel)
    extends KeyFactoryImpl with HashKeyFactory {

  def make(id: String) = new HashKeyImpl(getKey(id), this)

  def getMulti(prop: String, ids: Iterable[String])(implicit p: Pipeline): Future[Map[String, Option[String]]] = {
    if (isDebugEnabled) log("HGET-MULTI", s"prop: $prop, ids: $ids")
    ids.size > 0 match {
      case true =>
        val futures = ids.map(id => {
          if (isDebugEnabled) log("HGET", getKey(id), s"prop: $prop")
          guavaFutureToAkka(p.hget(getKey(id), prop))
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

  def mGetMulti(props: Array[String],
                ids: Iterable[String])(implicit p: Pipeline): Future[Map[String, Map[String, Option[String]]]] = {
    if (isDebugEnabled) log("HMGET-MULTI (start)", "props: " + props.map(_.toString).toList + s" ids: $ids")
    ids.size > 0 match {
      case true =>
        val futures = ids.map(id => {
          if (isDebugEnabled) log("HMGET", getKey(id), "props: " + props.map(_.toString).toList)
          val args = getKey(id) +: props.map(_.asInstanceOf[AnyRef])
          guavaFutureToAkka(p.hmget_(args: _*))
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

  def incrbyMulti(prop: String, values: Map[String, Long])(implicit p: Pipeline): Future[Map[String, Long]] = {
    if (isDebugEnabled) log("HINCRBY-MULTI", s"prop: $prop, values: $values")
    val keys = values.keys.toList

    keys.length > 0 match {
      case true =>
        val futures = keys.map { id =>
          if (isDebugEnabled) log("HINCRBY", getKey(id), s"prop: $prop, value: ${values(id)}")
          guavaFutureToAkka(p.hincrby(getKey(id), prop, values(id))).map { v =>
            id -> Math.round(v.data().toDouble)
          }
        }.toList // has to be list or gets interpreted as set later, killing duplicate results
        Future.sequence(futures).map(_.toMap)
      case false => Future.successful(Map())
    }
  }

}
