/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 11:06 PM
 */

package base.entity.kv.impl

import base.entity.kv.Key.Pipeline
import base.entity.kv.ListKey

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

}

