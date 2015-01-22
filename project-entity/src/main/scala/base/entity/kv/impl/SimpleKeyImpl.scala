/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 12:24 PM
 */

package base.entity.kv.impl

import base.entity.kv.{ Key, SimpleKey }

/**
 * Base model for standard keys
 */
// scalastyle:off null
abstract class SimpleKeyImpl[T] extends KeyImpl with SimpleKey[T] {

  def get = p.get(token).map { v =>
    val res = v match {
      case null                => None
      case v if v.data == null => None
      case v                   => Option(toType(v.data()))
    }
    if (isDebugEnabled) log("GET", s"value: $res")
    res
  }

  def set(v: T) = {
    val res = p.set(token, fromType(v)).map(_.data() == Key.STATUS_OK)
    if (isDebugEnabled) log("SET", s"value: $v, result: $res")
    res
  }

}

