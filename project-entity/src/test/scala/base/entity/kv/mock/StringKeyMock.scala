/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 7:44 PM
 */

package base.entity.kv.mock

import base.common.random.RandomService
import base.entity.kv.Key.Pipeline
import base.entity.kv.{ Key, StringKey }

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of StringKeyMock here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class StringKeyMock(val token: String = RandomService().md5.toString,
                    getResult: Future[Option[String]] = Future.successful(None),
                    setResult: Future[Boolean] = Future.successful(true),
                    keyMock: Key = new KeyMock()) extends StringKey {

  def get(implicit p: Pipeline) = getResult

  def set(v: String)(implicit p: Pipeline) = setResult

  def exists()(implicit p: Pipeline) = keyMock.exists()

  def expire(seconds: Long)(implicit p: Pipeline) = keyMock.expire(seconds)

  def ttl()(implicit p: Pipeline) = keyMock.ttl()

  def del()(implicit p: Pipeline) = keyMock.del()

}
