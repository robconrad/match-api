/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/10/15 8:43 PM
 */

package base.entity.kv.impl

import base.common.lib.Dispatchable
import base.entity.kv.ScredisKey
import base.entity.kv.bytea.ScredisSerializers
import scredis.commands.KeyCommands
import scredis.serialization.Writer

trait ScredisKeyImpl[K] extends ScredisKey[K] with Dispatchable {

  protected def keyCommands: KeyCommands

  implicit protected def mk: Manifest[K]

  implicit protected def keyWriter: Writer[K] = ScredisSerializers.writer[K]

  final lazy val key = keyPrefix.toBytes ++ keyWriter.write(keyValue)

  def del() =
    keyCommands.del(key).map(_ == 1)

  def dump =
    keyCommands.dump(key)

  def exists =
    keyCommands.exists(key)

  def expire(ttlSeconds: Int) =
    keyCommands.expire(key, ttlSeconds)

  def expireAt(timestamp: Long) =
    keyCommands.expireAt(key, timestamp)

  def move(database: Int) =
    keyCommands.move(key, database)

  def objectRefCount =
    keyCommands.objectRefCount(key)

  def objectEncoding =
    keyCommands.objectEncoding(key)

  def objectIdleTime =
    keyCommands.objectIdleTime(key)

  def persist() =
    keyCommands.persist(key)

  def pExpire(ttlMillis: Long) =
    keyCommands.pExpire(key, ttlMillis)

  def pExpireAt(timestampMillis: Long) =
    keyCommands.pExpireAt(key, timestampMillis)

  def pTtl =
    keyCommands.pTtl(key)

  def rename(newKey: K) =
    keyCommands.rename(key, newKey)

  def renameNX(newKey: K) =
    keyCommands.renameNX(key, newKey)

  def ttl =
    keyCommands.ttl(key)

  def `type` =
    keyCommands.`type`(key)

}
