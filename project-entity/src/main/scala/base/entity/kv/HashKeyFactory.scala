/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 1:10 PM
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
trait HashKeyFactory extends KeyFactory {

  final def factoryManifest = manifest[HashKeyFactory]

  def make(id: Id): HashKey

  def getMulti(prop: Prop, ids: Iterable[Id])(implicit p: Pipeline): Future[Map[Id, Option[String]]]

  def mGetMulti(props: Array[Prop],
                ids: Iterable[Id])(implicit p: Pipeline): Future[Map[Id, Map[Prop, Option[String]]]]

  def incrbyMulti(prop: Prop, values: Map[Id, Long])(implicit p: Pipeline): Future[Map[Id, Long]]

}
