/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/13/15 9:50 PM
 */

package base.entity.kv.impl

import java.nio.charset.Charset

import base.entity.kv.Key._
import base.entity.kv._

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
abstract class HashKeyServiceImpl[T <: Key] extends KeyServiceImpl[T] with HashKeyService[T] {

  def make(id: Id): T

  def getMulti(prop: Prop, ids: Iterable[Id])(implicit p: Pipeline): Future[Map[Id, Option[String]]] = {
    if (isDebugEnabled) log("HGET-MULTI", s"prop: $prop, ids: $ids")
    ids.size > 0 match {
      case true =>
        val futures = ids.map(id => {
          if (isDebugEnabled) log("HGET", getKey(id), s"prop: $prop")
          guavaFutureToAkka(p.hget(getKey(id), prop))
        }).toList // has to be list or gets interpreted as set later, killing duplicate results
        Future.sequence(futures).map { v =>
          ids.zip(v).map {
            case (key, value) => key -> Option(value.asAsciiString())
          }.toMap
        }
      case false => Future.successful(Map())
    }
  }

  def mGetMulti(props: Array[Prop],
                ids: Iterable[Id])(implicit p: Pipeline): Future[Map[Id, Map[Prop, Option[String]]]] = {
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
            (props zip res)(breakOut).map {
              case (key, value) => key -> Option(value)
            }.toMap[Prop, Option[String]]
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

  def incrbyMulti(prop: Prop, values: Map[Id, Long])(implicit p: Pipeline): Future[Map[Id, Long]] = {
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
