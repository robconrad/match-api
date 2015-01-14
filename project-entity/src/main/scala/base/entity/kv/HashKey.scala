/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/13/15 10:10 PM
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

  def create()(implicit p: Pipeline): Future[Boolean]

  def token = key.token

  def exists()(implicit p: Pipeline) = key.exists()

  def del()(implicit p: Pipeline) = key.del()

  def expire(seconds: Long)(implicit p: Pipeline) = key.expire(seconds)

  def ttl()(implicit p: Pipeline) = key.ttl()

}
