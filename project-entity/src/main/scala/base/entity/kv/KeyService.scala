/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 12:36 PM
 */

package base.entity.kv

import base.common.service.Service
import base.entity.kv.Key._

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of KeyFactory here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait KeyService[A, B <: Key] extends Service with KeyLogger {

  protected val CHANNEL: String
  final lazy protected val PREFIX = CHANNEL + PREFIX_DELIM

  protected def getKey(id: A): String

  def make(id: A)(implicit p: Pipeline): B

  def del(items: Iterable[B])(implicit p: Pipeline): Future[Int]

}
