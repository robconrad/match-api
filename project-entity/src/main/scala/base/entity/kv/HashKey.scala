/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:16 PM
 */

package base.entity.kv

import base.entity.kv.Key.Pipeline

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of ConcreteKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait HashKey extends Key {

  protected def key: PrivateHashKey

  def create(): Future[Boolean]

  def token = key.token

  def exists() = key.exists()

  def del() = key.del()

  def expire(seconds: Long) = key.expire(seconds)

  def ttl() = key.ttl()

}
