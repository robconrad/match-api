/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 10:25 PM
 */

package base.entity.kv.impl

import base.entity.kv.{ KeyCommandsService, ListKey }

abstract class ListKeyImpl[K, V](implicit val mk: Manifest[K], val mv: Manifest[V])
    extends KeyValueImpl[K, V]
    with ListKey[K, V] {

  private lazy val commands = KeyCommandsService().listCommands

  protected def keyCommands = commands

  def lIndex(index: Long) =
    commands.lIndex(key, index)

  def lInsert(position: scredis.Position, pivot: V, value: V) =
    commands.lInsert(key, position, pivot, value)

  def lLen =
    commands.lLen(key)

  def lPop =
    commands.lPop(key)

  def lPush(values: V*) =
    commands.lPush(key, values: _*)

  def lPushX(value: V) =
    commands.lPushX(key, value)

  def lRange(start: Long, stop: Long) =
    commands.lRange(key, start, stop)

  def lRem(value: V, count: Int) =
    commands.lRem(key, value, count)

  def lSet(index: Long, value: V) =
    commands.lSet(key, index, value)

  def lTrim(start: Long, stop: Long) =
    commands.lTrim(key, start, stop)

  def rPop =
    commands.rPop(key)

  def rPopLPush(destKey: ListKey[_, V]) =
    commands.rPopLPush(key, destKey.key)

  def rPush(values: V*) =
    commands.rPush(key, values: _*)

  def rPushX(value: V) =
    commands.rPushX(key, value)

}
