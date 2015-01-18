/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 11:42 AM
 */

package base.entity.kv.impl

import java.nio.charset.Charset

import base.entity.kv.Key.Pipeline
import base.entity.kv.{ PrivateHashKey, KeyLogger, SetKey }
import redis.client.RedisException
import redis.reply.{ MultiBulkReply, BulkReply }

import scala.collection.JavaConversions._

/**
 * Base model for set keys
 */
// scalastyle:off null
abstract class SetKeyImpl extends KeyImpl with SetKey {

  def members()(implicit p: Pipeline) = {
    p.smembers(token).map { v =>
      val res = v.asStringSet(Charset.defaultCharset()).toSet
      if (isDebugEnabled) log("SMEMBERS", "props: " + res.toString)
      res
    }
  }

  def isMember(value: Any)(implicit p: Pipeline) = {
    p.sismember(token, value).map { v =>
      val isMember = v.data() == 1L
      if (isDebugEnabled) log("SISMEMBER", s"value: $value res: $isMember")
      isMember
    }
  }

  def rand()(implicit p: Pipeline) = {
    p.srandmember_(token).map { v =>
      val res = v match {
        case v if v == null || v.data() == null => None
        case v                                  => Some(v.asInstanceOf[BulkReply].asUTF8String())
      }
      if (isDebugEnabled) log("SRANDMEMBER", s"result: $res")
      res
    }
  }

  def rand(count: Int)(implicit p: Pipeline) = {
    p.srandmember_(token, count.asInstanceOf[AnyRef]).map {
      case v: MultiBulkReply =>
        val res: Set[String] = v match {
          case v if v == null || v.data() == null => Set()
          case v                                  => v.asStringSet(Charset.defaultCharset()).toSet
        }
        if (isDebugEnabled) log("SRANDMEMBER", s"count: $count, result: $res")
        res
      case v => throw new RedisException(s"SRANDMEMBER got something other than MultiBulkReply: $v")
    }
  }

  def pop()(implicit p: Pipeline) = {
    p.spop(token).map { v =>
      val res = v.asUTF8String()
      if (isDebugEnabled) log("SPOP", s"result: $res")
      res match {
        case null => None
        case x    => Some(x)
      }
    }
  }

  def add(value: Any*)(implicit p: Pipeline) = {
    val args = token +: value.map(_.asInstanceOf[AnyRef])
    p.sadd_(args: _*).map { v =>
      val res = v.data().toInt
      if (isDebugEnabled) log("SADD", s" value: $value, result: $res")
      res
    }
  }

  def remove(value: Any)(implicit p: Pipeline) =
    p.srem_(token, value.asInstanceOf[AnyRef]).map { v =>
      val res = v.data().toInt > 0
      if (isDebugEnabled) log("SREM", s" value: $value, result: $res")
      res
    }

  def move(to: SetKey, member: Any)(implicit p: Pipeline) =
    p.smove(token, to.token, member).map { v =>
      val res = v.data().toInt
      if (isDebugEnabled) log("SMOVE", s" to: ${to.token} value: $member, result: $res")
      res > 0
    }

  def diffStore(sets: SetKey*)(implicit p: Pipeline) = {
    p.sdiffstore(token, sets.map(_.token): _*).map { v =>
      val res = v.data().toInt
      if (isDebugEnabled) log("SDIFFSTORE", s" sets: $sets, result: $res")
      res
    }
  }

}

