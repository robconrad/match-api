/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 10:47 AM
 */

package base.entity.kv.impl

import base.common.lib.{ Dispatchable, GuavaFutures }
import base.common.logging.Loggable
import base.common.service.CommonService
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
    val res = p.exists(token).map(_.data().intValue() == 1)
    if (isDebugEnabled) log("EXISTS", s"result: $res")
    res
  }

  def del() =
    p.del_(token).map { v =>
      val res = v.data().intValue()
      if (isDebugEnabled) log("DEL", s"result: $res")
      res == 1
    }

  def expire(seconds: Long) = {
    val res = p.expire(token, seconds).map(_.data().intValue() == 1)
    if (isDebugEnabled) log("EXPIRE", s"$seconds seconds, result: $res")
    res
  }

  def ttl() = {
    val res = p.ttl(token).map(_.data().longValue() match {
      case n if n < 0 => None
      case n          => Option(n)
    })
    if (isDebugEnabled) log("TTL", s"$res seconds")
    res
  }

  private[impl] def log(cmd: String, msg: String = "") {
    logger.log(cmd, token, msg)
  }

  def tokenToString = logger.tokenToString(token)

}

