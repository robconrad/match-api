/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 3:23 PM
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

  def exists()(implicit p: Pipeline) = {
    if (isDebugEnabled) log("EXISTS")
    p.exists(token).map(_.data().intValue() == 1)
  }

  def del()(implicit p: Pipeline) = {
    if (isDebugEnabled) log("DEL")
    p.del_(token).map(_.data().intValue() == 1)
  }

  def expire(seconds: Long)(implicit p: Pipeline) = {
    if (isDebugEnabled) log("EXPIRE", s"$seconds seconds")
    p.expire(token, seconds).map(_.data().intValue() == 1)
  }

  private[impl] def log(cmd: String, msg: String = "") {
    logger.log(cmd, token, msg)
  }

}

