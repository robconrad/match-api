/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 10:25 PM
 */

package base.entity.kv.impl

import base.entity.kv.{ KeyCommandsService, SimpleKey }

import scala.concurrent.duration.FiniteDuration

abstract class SimpleKeyImpl[K, V](implicit val mk: Manifest[K], val mv: Manifest[V])
    extends KeyValueImpl[K, V]
    with SimpleKey[K, V] {

  private lazy val commands = KeyCommandsService().stringCommands

  protected def keyCommands = commands

  def append(value: V) =
    commands.append(key, value)

  def bitCount(start: Long, stop: Long) =
    commands.bitCount(key, start, stop)

  def bitPos(bit: Boolean, start: Long, stop: Long) =
    commands.bitPos(key, bit, start, stop)

  def decr =
    commands.decr(key)

  def decrBy(decrement: Long) =
    commands.decrBy(key, decrement)

  def get =
    commands.get(key)

  def getBit(offset: Long) =
    commands.getBit(key, offset)

  def getRange(start: Long, stop: Long) =
    commands.getRange(key, start, stop)

  def getSet(value: V) =
    commands.getSet(key, value)

  def incr =
    commands.incr(key)

  def incrBy(increment: Long) =
    commands.incrBy(key, increment)

  def incrByFloat(increment: Double) =
    commands.incrByFloat(key, increment)

  def pSetEX(value: V, ttlMillis: Long) =
    commands.pSetEX(key, value, ttlMillis)

  def set(value: V, ttlOpt: Option[FiniteDuration], conditionOpt: Option[scredis.Condition]) =
    commands.set(key, value, ttlOpt, conditionOpt)

  def setBit(offset: Long, bit: Boolean) =
    commands.setBit(key, offset, bit)

  def setEX(value: V, ttlSeconds: Int) =
    commands.setEX(key, value, ttlSeconds)

  def setNX(value: V) =
    commands.setNX(key, value)

  def setRange(offset: Long, value: V) =
    commands.setRange(key, offset, value)

  def strLen =
    commands.strLen(key)

}
