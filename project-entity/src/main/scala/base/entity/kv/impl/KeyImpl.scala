/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/3/15 2:05 PM
 */

package base.entity.kv.impl

import base.common.lib.{ Dispatchable, GuavaFutures }
import base.common.logging.Loggable
import base.common.server.ServerService
import base.entity.kv.Key.Pipeline
import base.entity.kv.{ Key, KeyLogger }

/**
 * Base class of a data model that relies on redis.
 *  Requires an id parameter that is a unique identifier of some kind
 */
// scalastyle:off null
private[impl] abstract class KeyImpl extends Key with GuavaFutures with Loggable with Dispatchable {

  implicit val timeout = ServerService().defaultTimeout

  protected val logger: KeyLogger

  def exists()(implicit p: Pipeline) = {
    if (isDebugEnabled) log("EXISTS")
    p.exists(key).map(_.data().intValue() == 1)
  }

  def del()(implicit p: Pipeline) = {
    if (isDebugEnabled) log("DEL")
    p.del_(key).map(_.data().intValue() == 1)
  }

  def expire(seconds: Long)(implicit p: Pipeline) = {
    if (isDebugEnabled) log("EXPIRE", s"$seconds seconds")
    p.expire(key, seconds).map(_.data().intValue() == 1)
  }

  private[impl] def log(cmd: String, msg: String = "") {
    logger.log(cmd, key, msg)
  }

}

