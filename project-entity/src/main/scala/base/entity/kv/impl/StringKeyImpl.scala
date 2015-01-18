/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 11:05 AM
 */

package base.entity.kv.impl

import base.entity.kv.Key.Pipeline
import base.entity.kv.{ Key, StringKey }

import scala.concurrent.Future

/**
 * Base model for standard keys
 */
abstract class StringKeyImpl extends KeyImpl with StringKey {

  def get(implicit p: Pipeline): Future[Option[String]] = p.get(token).map { v =>
    val res = v.asUTF8String()
    if (isDebugEnabled) log("GET", s"value: $res")
    Option(res)
  }

  def set(v: String)(implicit p: Pipeline) = {
    if (isDebugEnabled) log("SET", s"value: $v")
    p.set(token, v).map(_.data() == Key.STATUS_OK)
  }

}

