/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 12:55 PM
 */

package base.entity.kv

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of SetKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait SetKey[T] extends Key {

  def members(): Future[Set[T]]

  def isMember(value: T): Future[Boolean]

  def rand(): Future[Option[T]]

  def rand(count: Int): Future[Set[T]]

  def pop(): Future[Option[T]]

  def add(value: T*): Future[Long]

  def remove(value: T): Future[Long]

  def move(to: SetKey[T], member: T): Future[Boolean]

  def diffStore(sets: SetKey[T]*): Future[Long]

}
