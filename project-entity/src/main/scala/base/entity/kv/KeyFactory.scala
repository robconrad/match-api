/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 1:28 PM
 */

package base.entity.kv

import base.entity.kv.Key._

import scala.concurrent.Future
import scala.reflect.Manifest

/**
 * {{ Describe the high level purpose of KeyFactory here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait KeyFactory extends KeyLogger {

  def factoryManifest: Manifest[_]

  protected def locator: KeyFactoryLocator[_ <: KeyFactory]
  final protected val CHANNEL = locator.prefix
  final protected val PREFIX = CHANNEL + PREFIX_DELIM

  private[kv] def getKey(id: Id): String

  def make(id: Id): Key

  def del(items: Iterable[Key])(implicit p: Pipeline): Future[Int]

}
