/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 12:59 PM
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
class KvFactoryServiceImplTest extends EntityServiceTest {

  private val clientCount = 10
  private val host = "0.0.0.0"
  private val port = 6379

  val service = new KvFactoryServiceImpl(clientCount, host, port)

  private val id = "id"
  private def token(prefix: String) = s"$prefix-$id"

  test("client") {
    assert(service.client.isInstanceOf[RedisClient])
  }

  test("pipeline") {
    assert(service.pipeline.isInstanceOf[RedisClient#Pipeline])
  }

}
