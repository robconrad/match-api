/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:42 PM
 */

package base.entity.kv.mock

import base.common.random.RandomService
import base.entity.kv.{ KvFactoryService, Key, IntKey }
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
                 keyMock: Key = new KeyMock()(KvFactoryService().pipeline))(implicit protected val p: Pipeline)
    extends IntKey {

  def increment() = incrResult

  def set(v: Int) = setResult

  def get() = getResult

  def exists() = keyMock.exists()

  def expire(seconds: Long) = keyMock.expire(seconds)

  def ttl() = keyMock.ttl()

  def del() = keyMock.del()

}
