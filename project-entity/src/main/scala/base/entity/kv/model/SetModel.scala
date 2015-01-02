/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/1/15 9:28 PM
 */

package base.entity.kv.model

import java.nio.charset.Charset

import redis.reply.BulkReply

import scala.collection.JavaConversions._
import scala.concurrent.Future

/**
 * Base model for set keys
 *
 * @param id unique identifier of the set
 */
// scalastyle:off null
abstract class SetModel(_OBJECT: SetObject, id: String) extends RedisModel(_OBJECT, id) {

  def members() = {
    if (isDebugEnabled) log("SMEMBERS (start)")
    pipeline.smembers(key).map { v =>
      val res = v.asStringSet(Charset.defaultCharset()).toSet
      if (isDebugEnabled) log("SMEMBERS (finish)", "props: " + res.toString)
      res
    }
  }

  def isMember(value: Any) = {
    if (isDebugEnabled) log("SISMEMBER (start)", s"value: $value")
    pipeline.sismember(key, value).map { v =>
      val isMember = v.data() == 1L
      if (isDebugEnabled) log("SISMEMBER (finish)", s"value: $value res: $isMember")
      isMember
    }
  }

  // note will only work for 1 randmember with this impl
  def rand() = {
    if (isDebugEnabled) log("SRANDMEMBER (start)")
    pipeline.srandmember_(key).map { v =>
      val res = v match {
        case null                  => None
        case v if v.data() == null => None
        case v                     => Some(v.asInstanceOf[BulkReply].asAsciiString())
      }
      if (isDebugEnabled) log("SRANDMEMBER (finish)", s"result: $res")
      res
    }
  }

  def pop() = {
    if (isDebugEnabled) log("SPOP (start)")
    pipeline.spop(key).map { v =>
      val res = v.asAsciiString()
      if (isDebugEnabled) log("SPOP (finish)", s"result: $res")
      res match {
        case null => None
        case x    => Some(x)
      }
    }
  }

  def add(value: Any) = pipeline.sadd_(key, value.asInstanceOf[AnyRef]).map { v =>
    val res = v.data().toInt > 0
    if (isDebugEnabled) log("SADD", s" value: $value, result: $res")
    res
  }

  def remove(value: Any) = pipeline.srem_(key, value.asInstanceOf[AnyRef]).map { v =>
    val res = v.data().toInt > 0
    if (isDebugEnabled) log("SREM", s" value: $value, result: $res")
    res
  }

  def move(to: SetModel, member: Any) = {
    val newToken = _OBJECT.getKey(to.id)
    if (isDebugEnabled) log("SMOVE", s" to: $newToken value: $member")
    pipeline.smove(key, newToken, member).map(_.data().toInt > 0)
  }

}

abstract class SetObject extends RedisObject {

  def remove(sets: List[SetModel], value: Any) = {
    if (isDebugEnabled) log("SREM-MULTI", s"sets: ${sets.map(s => getKey(s.id))}, value: $value")
    sets.length > 0 match {
      case true =>
        val futures = sets.map(set => guavaFutureToAkka(pipeline.srem_(getKey(set.id), value.asInstanceOf[AnyRef])))
        Future.sequence(futures).map(_.map(_.data().intValue() > 0).foldLeft(true)((b, v) => b && v))
      case false =>
        Future.successful(true)
    }
  }

  def count(sets: List[SetModel]): Future[Map[SetModel, Int]] = {
    if (isDebugEnabled) log("SCARD-MULTI", s"sets: ${sets.map(s => getKey(s.id))}")
    sets.length > 0 match {
      case true =>
        val futures = sets.map(set => guavaFutureToAkka(pipeline.scard(getKey(set.id))))
        Future.sequence(futures).map { v =>
          sets.zip(v.map(_.data().intValue())).toMap
        }
      case false =>
        Future.successful(Map())
    }
  }

  def unionStore(destination: String, keys: String*) = {
    if (isDebugEnabled) log("SUNIONSTORE", s"destination: $destination, keys: $keys")
    keys.length > 0 match {
      case true  => pipeline.sunionstore(destination, keys.toArray).map(_.data().intValue())
      case false => Future.successful(0)
    }
  }

}
