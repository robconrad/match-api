/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 12:36 PM
 */

package base.entity.kv

import base.entity.kv.Key._

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of HashKeyFactory here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait HashKeyService[A, B <: Key] extends KeyService[A, B] {

  def make(id: A)(implicit p: Pipeline): B

  def getMulti(prop: Prop, ids: Iterable[A])(implicit p: Pipeline): Future[Map[A, Option[String]]]

  def mGetMulti(props: Array[Prop],
                ids: Iterable[A])(implicit p: Pipeline): Future[Map[A, Map[Prop, Option[String]]]]

  def incrbyMulti(prop: Prop, values: Map[A, Long])(implicit p: Pipeline): Future[Map[A, Long]]

}
