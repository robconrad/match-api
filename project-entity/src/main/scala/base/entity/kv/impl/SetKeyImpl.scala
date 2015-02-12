/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/10/15 8:44 PM
 */

package base.entity.kv.impl

import base.entity.kv.{ ScredisFactoryService, SetKey }

abstract class SetKeyImpl[K, V](implicit val mk: Manifest[K], val mv: Manifest[V])
    extends ScredisKeyValueImpl[K, V]
    with SetKey[K, V] {

  private lazy val commands = ScredisFactoryService().setCommands

  protected def keyCommands = commands

  def add(members: V*) =
    commands.sAdd(key, members: _*)

  def card =
    commands.sCard(key)

  def diff(keys: SetKey[_, V]*) =
    commands.sDiff(key, keys.map(_.key): _*)

  def diffStore(key1: SetKey[_, V], keys: SetKey[_, V]*) =
    commands.sDiffStore(key, key1.key, keys.map(_.key): _*)

  def inter(keys: SetKey[_, V]*) =
    commands.sInter(key +: keys.map(_.key): _*)

  def interStore(keys: SetKey[_, V]*) =
    commands.sInterStore(key, keys.map(_.key): _*)

  def isMember(member: V) =
    commands.sIsMember(key, member)

  def members =
    commands.sMembers(key)

  def move(destKey: SetKey[_, V], member: V) =
    commands.sMove(key, destKey.key, member)

  def pop() =
    commands.sPop(key)

  def randMember =
    commands.sRandMember(key)

  def randMembers(count: Int) =
    commands.sRandMembers(key, count)

  def rem(members: V*) =
    commands.sRem(key, members: _*)

  def scan(cursor: Long, matchOpt: Option[String], countOpt: Option[Int]) =
    commands.sScan(key, cursor, matchOpt, countOpt)

  def union(keys: SetKey[_, V]*) =
    commands.sUnion(key +: keys.map(_.key): _*)

  def unionStore(keys: SetKey[_, V]*) =
    commands.sUnionStore(key, keys.map(_.key): _*)

}
