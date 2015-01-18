/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 6:33 PM
 */

package base.entity.kv.impl

import java.nio.charset.Charset

import base.entity.kv.Key.Pipeline
import base.entity.kv.ListKey
import scala.collection.JavaConversions._

/**
 * Base model for set keys
 */
// scalastyle:off null
abstract class ListKeyImpl extends KeyImpl with ListKey {

  def prepend(value: Any*)(implicit p: Pipeline) = {
    val args = token +: value.map(_.asInstanceOf[AnyRef])
    p.lpush_(args: _*).map { v =>
      val res = v.data().toInt > 0
      if (isDebugEnabled) log("LPUSH", s" value: $value, result: $res")
      res
    }
  }

  def prependIfExists(value: Any)(implicit p: Pipeline) = {
    p.lpushx(token, value).map { v =>
      val res = v.data().toInt > 0
      if (isDebugEnabled) log("LPUSHX", s" value: $value, result: $res")
      res
    }
  }

  def range(start: Int, stop: Int)(implicit p: Pipeline) = {
    p.lrange(token, start, stop).map { v =>
      val res = v.asStringList(Charset.defaultCharset()).toList
      if (isDebugEnabled) log("LRANGE", s" start: $start, stop: $stop, resukt: $res")
      res
    }
  }

}

