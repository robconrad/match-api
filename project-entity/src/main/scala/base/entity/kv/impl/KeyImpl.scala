/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:16 PM
 */

package base.entity.kv.impl

import base.common.lib.{ Dispatchable, GuavaFutures }
import base.common.logging.Loggable
import base.common.service.CommonService
import base.entity.kv.Key.Pipeline
import base.entity.kv.{ Key, KeyLogger }

/**
 * Base class of a data model that relies on redis.
 *  Requires an id parameter that is a unique identifier of some kind
 */
// scalastyle:off null
private[impl] abstract class KeyImpl extends Key with GuavaFutures with Loggable with Dispatchable {

  implicit val timeout = CommonService().defaultTimeout

  protected val logger: KeyLogger

  def exists() = {
    if (isDebugEnabled) log("EXISTS")
    p.exists(token).map(_.data().intValue() == 1)
  }

  def del() = {
    if (isDebugEnabled) log("DEL")
    p.del_(token).map(_.data().intValue() == 1)
  }

  def expire(seconds: Long) = {
    if (isDebugEnabled) log("EXPIRE", s"$seconds seconds")
    p.expire(token, seconds).map(_.data().intValue() == 1)
  }

  def ttl() = {
    if (isDebugEnabled) log("TTL")
    p.ttl(token).map(_.data().longValue() match {
      case n if n < 0 => None
      case n          => Option(n)
    })
  }

  private[impl] def log(cmd: String, msg: String = "") {
    logger.log(cmd, token, msg)
  }

}

