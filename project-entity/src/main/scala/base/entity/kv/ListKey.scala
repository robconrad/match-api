/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 6:31 PM
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
trait ListKey extends Key {

  def prepend(value: Any*)(implicit p: Pipeline): Future[Boolean]

  def prependIfExists(value: Any)(implicit p: Pipeline): Future[Boolean]

  def range(start: Int, stop: Int)(implicit p: Pipeline): Future[List[String]]

}
