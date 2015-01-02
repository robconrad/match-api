/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/27/14 1:27 PM
 */

package base.entity.kv.model

import base.common.lib.{ Dispatchable, GuavaFutures, Tryo }
import base.common.logging.Loggable
import base.entity.kv.KvService

abstract class RedisObject extends GuavaFutures with Loggable with Dispatchable {

  final val ID = "id"
  final val PREFIX_DELIM = "-"

  final val STATUS_OK = "OK"

  val CHANNEL: String
  lazy val PREFIX = CHANNEL + PREFIX_DELIM

  implicit def boolean2Int(b: Boolean) = if (b) 1 else 0
  implicit def int2Boolean(i: Int) = i > 0
  implicit def string2Boolean(s: String): Boolean = Tryo(s.toInt, false)

  private[model] def client = KvService().client
  private[model] def pipeline = client.pipeline()

  private[model] def getKey(token: String) = {
    assert(token.length > 0 && token.length < 1000)
    PREFIX + token
  }

  private[model] def log(cmd: String, msg: String) {
    if (isDebugEnabled) log(cmd, CHANNEL, msg)
  }

  private[model] def log(cmd: String, token: String, msg: String = "") {
    if (isDebugEnabled) debug(s"Redis.$cmd:: token: $token, $msg")
  }

  def del(items: Iterable[RedisModel]) = {
    if (isDebugEnabled) log("DEL-MULTI", s"items: $items")
    pipeline.del(items.map { case i => getKey(i.id) }.toArray).map(_.data().intValue())
  }

}
