/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 4:53 PM
 */

package base.entity.kv.impl

import base.entity.kv.SetKey
import base.entity.kv.bytea.ByteaSerializers._
import redis.client.RedisException
import redis.reply.{ BulkReply, MultiBulkReply }

/**
 * Base model for set keys
 */
// scalastyle:off null
abstract class SetKeyImpl[T](implicit m: Manifest[T]) extends KeyImpl with SetKey[T] {

  def members() = {
    p.smembers(token).map { v =>
      val res = v.data().map { x =>
        deserialize[T](x.data().asInstanceOf[Array[Byte]])
      }.toSet
      if (isDebugEnabled) log("SMEMBERS", "res: " + res.toString)
      res
    }
  }

  def isMember(value: T) = {
    p.sismember(token, serialize(value)).map { v =>
      val isMember = v.data() == 1L
      if (isDebugEnabled) log("SISMEMBER", s"value: $value res: $isMember")
      isMember
    }
  }

  def rand() = {
    p.srandmember_(token).map { v =>
      val res = v match {
        case null                  => None
        case v if v.data() == null => None
        case v: BulkReply          => Option(deserialize(v.data()))
        case v                     => throw new RedisException(s"SRANDMEMBER received sth. other than BulkReply: $v")
      }
      if (isDebugEnabled) log("SRANDMEMBER", s"result: $res")
      res
    }
  }

  def rand(count: Int) = {
    p.srandmember_(token, count.asInstanceOf[AnyRef]).map {
      case null                  => Set()
      case v if v.data() == null => Set()
      case v: MultiBulkReply =>
        val res = v.data() map {
          case v: BulkReply => deserialize(v.data())
          case v            => throw new RedisException(s"SRANDMEMBER got something other than BulkReply: $v")
        }
        if (isDebugEnabled) log("SRANDMEMBER", s"count: $count, result: $res")
        res.toSet
      case v => throw new RedisException(s"SRANDMEMBER got something other than MultiBulkReply: $v")
    }
  }

  def pop() = {
    p.spop(token).map { v =>
      val res = v match {
        case null                  => None
        case v if v.data() == null => None
        case v                     => Option(deserialize(v.data()))
      }
      if (isDebugEnabled) log("SPOP", s"result: $res")
      res
    }
  }

  def add(value: T*) = {
    val args = token +: value.map(v => serialize(v))
    p.sadd_(args: _*).map { v =>
      val res = v.data()
      if (isDebugEnabled) log("SADD", s" value: $value, result: $res")
      res
    }
  }

  def remove(value: T) =
    p.srem_(token, serialize(value)).map { v =>
      val res = v.data()
      if (isDebugEnabled) log("SREM", s" value: $value, result: $res")
      res
    }

  def move(to: SetKey[T], member: T) =
    p.smove(token, to.token, serialize(member)).map { v =>
      val res = v.data()
      if (isDebugEnabled) log("SMOVE", s" to: ${to.token} value: $member, result: $res")
      res > 0
    }

  def diffStore(sets: SetKey[T]*) = {
    p.sdiffstore(token, sets.map(_.token): _*).map { v =>
      val res = v.data()
      if (isDebugEnabled) log("SDIFFSTORE", s" sets: $sets, result: $res")
      res
    }
  }

}

