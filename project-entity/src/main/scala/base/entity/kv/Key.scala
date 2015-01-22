/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 12:51 PM
 */

package base.entity.kv

import base.common.lib.Tryo
import base.entity.kv.Key._
import redis.client.RedisClient

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of Key here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait Key {

  protected def p: Pipeline

  def token: String

  def exists(): Future[Boolean]

  def del(): Future[Boolean]

  def expire(seconds: Long): Future[Boolean]

  def ttl(): Future[Option[Long]]

  override lazy val toString = s"${getClass.getSimpleName}($token)"

}

object Key {

  type Pipeline = RedisClient#Pipeline
  type Prop = KeyProp[_]

  val ID = "id"
  val PREFIX_DELIM = "-"
  val STATUS_OK = "OK"

  def boolean2Int(b: Boolean) = if (b) 1 else 0
  def int2Boolean(i: Int) = i > 0
  def string2Boolean(s: String): Boolean = Tryo(int2Boolean(s.toInt), false)

}
