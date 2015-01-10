/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 3:23 PM
 */

package base.entity.kv.impl

import base.entity.kv.Key.Pipeline
import base.entity.kv.{ IntKey, KeyLogger }

/**
 * Base model for standard keys
 */
// scalastyle:off null
private[kv] final class IntKeyImpl(val token: String, protected val logger: KeyLogger) extends KeyImpl with IntKey {

  def incr()(implicit p: Pipeline) = {
    if (isDebugEnabled) log("INCR (start)")
    p.incr(token).map { v =>
      val res = v.data().toInt
      if (isDebugEnabled) log("INCR (finish)", s"value: $res")
      res
    }
  }

  def set(v: Int)(implicit p: Pipeline) = {
    if (isDebugEnabled) log("SET (start)", s"value: $v")
    p.set(token, v).map { v =>
      if (isDebugEnabled) log("SET (finish)", s"value: $v")
      true
    }
  }

  def get()(implicit p: Pipeline) = {
    if (isDebugEnabled) log("GET (start)")
    p.get(token).map {
      case res if res == null || res.data() == null => None
      case res                                      => Option(res.asAsciiString().toInt)
    }.map { res =>
      if (isDebugEnabled) log("GET (finish)", s"value: $res")
      res
    }
  }

}

