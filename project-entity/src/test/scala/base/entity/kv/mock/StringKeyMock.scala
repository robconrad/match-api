/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:42 PM
 */

package base.entity.kv.mock

import base.common.random.RandomService
import base.entity.kv.Key.Pipeline
import base.entity.kv.{ KvFactoryService, Key, StringKey }

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
                    keyMock: Key = new KeyMock()(KvFactoryService().pipeline))(implicit protected val p: Pipeline)
    extends StringKey {

  def get = getResult

  def set(v: String) = setResult

  def exists() = keyMock.exists()

  def expire(seconds: Long) = keyMock.expire(seconds)

  def ttl() = keyMock.ttl()

  def del() = keyMock.del()

}
