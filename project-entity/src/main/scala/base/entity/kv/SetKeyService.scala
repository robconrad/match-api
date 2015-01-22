/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 12:33 PM
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
trait SetKeyService[A, B <: SetKey[_]] extends KeyService[A, B] {

  def make(id: A)(implicit p: Pipeline): B

  def remove(sets: List[SetKey[_]], value: Any)(implicit p: Pipeline): Future[Map[SetKey[_], Boolean]]

  def count(sets: List[SetKey[_]])(implicit p: Pipeline): Future[Map[SetKey[_], Int]]

  def unionStore(destination: SetKey[_], sets: SetKey[_]*)(implicit p: Pipeline): Future[Int]

}
