/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 2:11 PM
 */

package base.entity.kv.impl

import base.common.lib.{ Dispatchable, GuavaFutures }
import base.common.logging.Loggable
import base.entity.kv.Key._
import base.entity.kv.{ KeyId, Key, KeyService }

private[impl] abstract class KeyServiceImpl[T <: Key]
    extends KeyService[T] with GuavaFutures with Loggable with Dispatchable {

  protected def getKey(keyId: Id) = {
    val id = keyId.toString
    assert(id.length > 0 && id.length < 1000)
    PREFIX + id
  }

  def log(cmd: String, msg: String) {
    if (isDebugEnabled) log(cmd, CHANNEL, msg)
  }

  def log(cmd: String, token: String, msg: String = "") {
    if (isDebugEnabled) debug(s"Redis.$cmd:: token: $token, $msg")
  }

  def del(items: Iterable[T])(implicit p: Pipeline) = {
    if (isDebugEnabled) log("DEL-MULTI", s"items: $items")
    p.del(items.map(_.token).toArray: _*).map(_.data().intValue())
  }

}
