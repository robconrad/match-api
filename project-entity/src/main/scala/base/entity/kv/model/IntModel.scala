/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/1/15 8:22 PM
 */

package base.entity.kv.model

/**
 * Base model for standard keys
 *
 * @param id unique identifier of the key
 */
// scalastyle:off null
abstract class IntModel(_OBJECT: IntObject, id: String) extends RedisModel(_OBJECT, id) {

  def incr() = {
    if (isDebugEnabled) log("INCR (start)")
    pipeline.incr(key).map { v =>
      val res = v.data().toInt
      if (isDebugEnabled) log("INCR (finish)", s"value: $res")
      res
    }
  }

  def set(v: Int) = {
    if (isDebugEnabled) log("SET (start)", s"value: $v")
    pipeline.set(key, v).map { v =>
      if (isDebugEnabled) log("SET (finish)", s"value: $v")
      true
    }
  }

  def get() = {
    if (isDebugEnabled) log("GET (start)")
    pipeline.get(key).map {
      case null                      => None
      case res if res.data() == null => None
      case res                       => Option(res.asAsciiString().toInt)
    }.map { res =>
      if (isDebugEnabled) log("GET (finish)", s"value: $res")
      res
    }
  }

}

abstract class IntObject extends RedisObject {

}
