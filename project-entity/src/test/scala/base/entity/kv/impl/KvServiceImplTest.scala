/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 12:44 PM
 */

package base.entity.kv.impl

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

  private val channel = "channel"
  private val id = "id"
  private val key = s"$channel-$id"

  test("client") {
    assert(service.client.isInstanceOf[RedisClient])
  }

  test("pipeline") {
    assert(service.pipeline.isInstanceOf[RedisClient#Pipeline])
  }

  test("make*Factory") {
    assert(service.makeHashKeyFactory(channel).make(id).key == key)
    assert(service.makeIntKeyFactory(channel).make(id).key == key)
    assert(service.makeSetKeyFactory(channel).make(id).key == key)
  }

}
