/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/3/15 1:13 PM
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

  def make(id: String): HashKey

  def getMulti(prop: String, ids: Iterable[String])(implicit p: Pipeline): Future[Map[String, Option[String]]]

  def mGetMulti(props: Array[String],
                ids: Iterable[String])(implicit p: Pipeline): Future[Map[String, Map[String, Option[String]]]]

  def incrbyMulti(prop: String, values: Map[String, Long])(implicit p: Pipeline): Future[Map[String, Long]]

}
