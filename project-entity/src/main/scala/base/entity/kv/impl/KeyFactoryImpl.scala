/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/3/15 4:23 PM
 */

package base.entity.kv.impl

import base.common.lib.{ Dispatchable, GuavaFutures }
import base.common.logging.Loggable
import base.entity.kv.Key._
import base.entity.kv.{ Key, KeyFactory }

private[impl] abstract class KeyFactoryImpl extends KeyFactory with GuavaFutures with Loggable with Dispatchable {

  private[kv] def getKey(token: String) = {
    assert(token.length > 0 && token.length < 1000)
    PREFIX + token
  }

  private[kv] def log(cmd: String, msg: String) {
    if (isDebugEnabled) log(cmd, CHANNEL, msg)
  }

  private[kv] def log(cmd: String, token: String, msg: String = "") {
    if (isDebugEnabled) debug(s"Redis.$cmd:: token: $token, $msg")
  }

  def del(items: Iterable[Key])(implicit p: Pipeline) = {
    if (isDebugEnabled) log("DEL-MULTI", s"items: $items")
    p.del(items.map(_.key).toArray: _*).map(_.data().intValue())
  }

}
