/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:16 PM
 */

package base.entity.kv.impl

import base.entity.kv.Key.Pipeline
import base.entity.kv.{ IntKey, KeyLogger }

/**
 * Base model for standard keys
 */
// scalastyle:off null
abstract class IntKeyImpl extends KeyImpl with IntKey {

  def increment() = {
    if (isDebugEnabled) log("INCR (start)")
    p.incr(token).map { v =>
      val res = v.data().toInt
      if (isDebugEnabled) log("INCR (finish)", s"value: $res")
      res
    }
  }

  def set(v: Int) = {
    if (isDebugEnabled) log("SET (start)", s"value: $v")
    p.set(token, v).map { v =>
      if (isDebugEnabled) log("SET (finish)", s"value: $v")
      true
    }
  }

  def get() = {
    if (isDebugEnabled) log("GET (start)")
    p.get(token).map {
      case res if res == null || res.data() == null => None
      case res                                      => Option(res.asUTF8String().toInt)
    }.map { res =>
      if (isDebugEnabled) log("GET (finish)", s"value: $res")
      res
    }
  }

}

