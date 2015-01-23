/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 3:45 PM
 */

package base.entity.kv.impl

import java.nio.ByteBuffer

import base.common.lib.{ Dispatchable, GuavaFutures }
import base.common.logging.Loggable
import base.entity.kv.Key._
import base.entity.kv.{ KeyPrefixes, Key, KeyService }

private[impl] abstract class KeyServiceImpl[A, B <: Key]
    extends KeyService[A, B] with GuavaFutures with Loggable with Dispatchable {

  private lazy val prefixBytes = prefix.toBytes

  final protected def getKey(keyId: A) = {
    prefixBytes ++ toBytes(keyId)
  }

  def log(cmd: String, msg: String) {
    if (isDebugEnabled) log(cmd, prefixBytes, msg)
  }

  def log(cmd: String, token: Array[Byte], msg: String = "") {
    if (isDebugEnabled) {
      val keyPrefix = KeyPrefixes.values.find(_.id == ByteBuffer.wrap(token.slice(0, 2)).getShort).getOrElse("unknown")
      debug(s"Redis.$cmd:: prefix: $keyPrefix-${fromBytes(token.slice(2, token.length))}, $msg")
    }
  }

  def del(items: Iterable[B])(implicit p: Pipeline) = {
    if (isDebugEnabled) log("DEL-MULTI", s"items: $items")
    p.del(items.map(_.token).toArray: _*).map(_.data().intValue())
  }

}
