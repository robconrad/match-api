/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 11:14 AM
 */

package base.entity.kv

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of SetKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait ListKey[T] extends TypedKey[T] {

  def prepend(value: T*): Future[Boolean]

  def prependIfExists(value: T): Future[Boolean]

  def range(start: Int, stop: Int): Future[List[T]]

}
