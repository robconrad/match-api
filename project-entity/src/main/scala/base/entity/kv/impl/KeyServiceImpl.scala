/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 1:05 PM
 */

package base.entity.kv.impl

import java.nio.ByteBuffer

import base.common.lib.{ Dispatchable, GuavaFutures }
import base.common.logging.Loggable
import base.entity.kv.Key._
import base.entity.kv.bytea.ByteaSerializers
import base.entity.kv.{ KeyPrefixes, Key, KeyService }

private[impl] abstract class KeyServiceImpl[A, B <: Key](implicit m: Manifest[A])
    extends KeyService[A, B]
    with GuavaFutures
    with Loggable
    with Dispatchable {

  private val prefixBytesLength = 2
  private lazy val prefixBytes = prefix.toBytes

  final protected def getKey(keyId: A) = {
    prefixBytes ++ ByteaSerializers.serialize(keyId)
  }

  def log(cmd: String, msg: String) {
    if (isDebugEnabled) log(cmd, prefixBytes, msg)
  }

  def log(cmd: String, token: Array[Byte], msg: String = "") {
    if (isDebugEnabled) {
      debug(s"Redis.$cmd:: prefix: ${tokenToString(token)}, $msg")
    }
  }

  def tokenToString(token: Array[Byte]) = {
    val keyPrefix = KeyPrefixes.values.find(_.id == ByteBuffer.wrap(token.slice(0, prefixBytesLength)).getShort)
    val keyString = ByteaSerializers.deserialize[A](token.slice(prefixBytesLength, token.length))
    s"${keyPrefix.getOrElse("unknown")}-$keyString"
  }

  def del(items: Iterable[B])(implicit p: Pipeline) = {
    if (isDebugEnabled) log("DEL-MULTI", s"items: $items")
    p.del(items.map(_.token).toArray: _*).map(_.data().intValue())
  }

}
