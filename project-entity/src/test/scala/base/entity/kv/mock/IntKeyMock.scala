/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 12:02 PM
 */

package base.entity.kv.mock

import base.common.random.RandomService
import base.entity.kv.{ Key, IntKey }
import base.entity.kv.Key.Pipeline

import scala.concurrent.Future

/**
 * {{ Describe the high level purpose of IntKeyMock here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class IntKeyMock(val token: String = RandomService().md5.toString,
                 incrResult: Future[Int] = Future.successful(1),
                 setResult: Future[Boolean] = Future.successful(true),
                 getResult: Future[Option[Int]] = Future.successful(None),
                 keyMock: Key = new KeyMock()) extends IntKey {

  def incr()(implicit p: Pipeline) = incrResult

  def set(v: Int)(implicit p: Pipeline) = setResult

  def get()(implicit p: Pipeline) = getResult

  def exists()(implicit p: Pipeline) = keyMock.exists()

  def expire(seconds: Long)(implicit p: Pipeline) = keyMock.expire(seconds)

  def del()(implicit p: Pipeline) = keyMock.del()

}
