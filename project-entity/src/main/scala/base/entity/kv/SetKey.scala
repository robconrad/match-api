/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:16 PM
 */

package base.entity.kv

import base.entity.kv.Key._

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of SetKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait SetKey extends Key {

  def members(): Future[Set[String]]

  def isMember(value: Any): Future[Boolean]

  def rand(): Future[Option[String]]

  def rand(count: Int): Future[Set[String]]

  def pop(): Future[Option[String]]

  def add(value: Any*): Future[Int]

  def remove(value: Any): Future[Boolean]

  def move(to: SetKey, member: Any): Future[Boolean]

  def diffStore(sets: SetKey*): Future[Int]

}
