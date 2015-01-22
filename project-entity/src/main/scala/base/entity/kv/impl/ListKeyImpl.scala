/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 11:18 AM
 */

package base.entity.kv.impl

import base.entity.kv.ListKey
import redis.client.RedisException
import redis.reply.BulkReply

/**
 * Base model for set keys
 */
// scalastyle:off null
abstract class ListKeyImpl[T] extends KeyImpl with ListKey[T] {

  def prepend(value: T*) = {
    val args = token +: value.map(fromType)
    p.lpush_(args: _*).map { v =>
      val res = v.data() > 0L
      if (isDebugEnabled) log("LPUSH", s" value: $value, result: $res")
      res
    }
  }

  def prependIfExists(value: T) = {
    p.lpushx(token, fromType(value)).map { v =>
      val res = v.data() > 0L
      if (isDebugEnabled) log("LPUSHX", s" value: $value, result: $res")
      res
    }
  }

  def range(start: Int, stop: Int) = {
    p.lrange(token, start, stop).map { v =>
      //val res = v.asStringList(Charset.defaultCharset()).toList
      val res = v match {
        case null                => List()
        case v if v.data == null => List()
        case v => v.data().map {
          case v: BulkReply => toType(v.data())
          case v            => throw new RedisException(s"LRANGE received unexpected type: $v")
        }.toList
      }
      if (isDebugEnabled) log("LRANGE", s" start: $start, stop: $stop, resukt: $res")
      res
    }
  }

}

