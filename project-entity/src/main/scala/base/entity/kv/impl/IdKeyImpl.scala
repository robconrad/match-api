/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 11:05 AM
 */

package base.entity.kv.impl

import java.util.UUID

import base.common.lib.{ Tryo, Ifo }
import base.entity.kv.Key.Pipeline
import base.entity.kv.{ IdKey, Key }

import scala.concurrent.Future

/**
 * Base model for standard keys
 */
// scalastyle:off null
abstract class IdKeyImpl extends KeyImpl with IdKey {

  def get(implicit p: Pipeline): Future[Option[UUID]] = p.get(token).map { v =>
    val res = v.asUTF8String()
    if (isDebugEnabled) log("GET", s"value: $res")
    res match {
      case null => None
      case _    => Tryo(UUID.fromString(res))
    }
  }

  def set(v: UUID)(implicit p: Pipeline) = {
    if (isDebugEnabled) log("SET", s"value: $v")
    p.set(token, v).map(_.data() == Key.STATUS_OK)
  }

}

