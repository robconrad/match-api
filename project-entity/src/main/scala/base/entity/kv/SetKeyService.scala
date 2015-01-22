/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 11:15 AM
 */

package base.entity.kv

import base.entity.kv.Key._

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of SetKeyFactory here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait SetKeyService[A, B <: SetKey[A]] extends KeyService[B] {

  def make(id: Id)(implicit p: Pipeline): B

  def remove(sets: List[SetKey[A]], value: Any)(implicit p: Pipeline): Future[Map[SetKey[A], Boolean]]

  def count(sets: List[SetKey[A]])(implicit p: Pipeline): Future[Map[SetKey[A], Int]]

  def unionStore(destination: SetKey[A], sets: SetKey[A]*)(implicit p: Pipeline): Future[Int]

}
