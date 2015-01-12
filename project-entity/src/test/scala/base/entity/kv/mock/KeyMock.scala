/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 4:36 PM
 */

package base.entity.kv.mock

import base.common.random.RandomService
import base.entity.kv.Key
import base.entity.kv.Key.Pipeline

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of KeyMock here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class KeyMock(val token: String = RandomService().md5.toString,
              expireResult: Future[Boolean] = Future.successful(true),
              ttlResult: Future[Option[Long]] = Future.successful(None),
              delResult: Future[Boolean] = Future.successful(true),
              existsResult: Future[Boolean] = Future.successful(true)) extends Key {

  def expire(seconds: Long)(implicit p: Pipeline) = expireResult

  def ttl()(implicit p: Pipeline) = ttlResult

  def del()(implicit p: Pipeline) = delResult

  def exists()(implicit p: Pipeline) = existsResult

}
