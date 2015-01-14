/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/13/15 10:09 PM
 */

package base.entity.kv

import base.entity.kv.Key._

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of IntKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait IntKey extends Key {

  def increment()(implicit p: Pipeline): Future[Int]

  def set(v: Int)(implicit p: Pipeline): Future[Boolean]

  def get()(implicit p: Pipeline): Future[Option[Int]]

}
