/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 1:39 PM
 */

package base.entity.kv.impl

import base.entity.kv._
import base.entity.service.EntityServiceTest
import redis.client.RedisClient

/**
 * {{ Describe the high level purpose of KvServiceImplTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class KvServiceImplTest extends EntityServiceTest {

  private val clientCount = 10
  private val host = "0.0.0.0"
  private val port = 6379

  val service = new KvServiceImpl(clientCount, host, port)

  private val id = KeyId("id")
  private def token(prefix: String) = s"$prefix-$id"

  test("client") {
    assert(service.client.isInstanceOf[RedisClient])
  }

  test("pipeline") {
    assert(service.pipeline.isInstanceOf[RedisClient#Pipeline])
  }

  test("make*Factory") {
    assert(service.makeHashKeyFactory(new KeyFactoryLocator[HashKeyFactory]("hash") {}).make(id).token == token("hash"))
    assert(service.makeIntKeyFactory(new KeyFactoryLocator[IntKeyFactory]("int") {}).make(id).token == token("int"))
    assert(service.makeSetKeyFactory(new KeyFactoryLocator[SetKeyFactory]("set") {}).make(id).token == token("set"))
    // duplicate prefix should throw
    intercept[RuntimeException] {
      service.makeSetKeyFactory(new KeyFactoryLocator[SetKeyFactory]("set") {}).make(id).token == token("set")
    }
  }

}
