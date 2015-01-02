/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/27/14 12:34 PM
 */

package base.entity.kv.model

import base.common.lib.{ Dispatchable, GuavaFutures }
import base.common.logging.Loggable
import base.common.server.ServerService
import base.entity.kv.KvService

/**
 * Base class of a data model that relies on redis.
 *  Requires an id parameter that is a unique identifier of some kind
 * @param _OBJECT static extension of ObjectModel specific to the data model
 * @param id unique identifier
 */
// scalastyle:off null
abstract class RedisModel(val _OBJECT: RedisObject, val id: String)
    extends GuavaFutures with Loggable with Dispatchable {

  // actor timeout
  implicit val timeout = ServerService().defaultTimeout

  lazy val key = _OBJECT.getKey(id)
  private[model] lazy val client = _OBJECT.client
  private[model] lazy val pipeline = client.pipeline()

  /**
   * Delete this key
   */
  protected def del() = {
    if (isDebugEnabled) log("DEL")
    pipeline.del_(key).map(_.data().intValue() == 1)
  }

  protected def expire(seconds: Long) = {
    if (isDebugEnabled) log("EXPIRE", s"$seconds seconds")
    pipeline.expire(key, seconds).map(_.data().intValue() == 1)
  }

  private[model] def log(cmd: String, msg: String = "") {
    _OBJECT.log(cmd, key, msg)
  }

}

