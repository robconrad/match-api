/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/27/14 12:31 PM
 */

package base.entity.kv.impl

import base.common.service.ServiceImpl
import base.entity.kv.KvService
import redis.client.RedisClient

/**
 * {{ Describe the high level purpose of KvServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class KvServiceImpl(clientCount: Int, host: String, port: Int) extends ServiceImpl with KvService {

  private val PING_RESPONSE = "PONG"

  private val clients = List.fill(clientCount)(makeClient())

  private var pointer = 0

  def client = {
    pointer = (pointer + 1) % clientCount
    clients(pointer)
  }

  private def makeClient() = {
    try {
      val redis = new RedisClient(host, port)
      val ping = redis.ping().data()

      if (ping != PING_RESPONSE) {
        throw new Exception("Pinging redis server returned unexpected response " + ping)
      }

      redis
    } catch {
      case e: Exception => throw new Exception("Unable to connect to and ping redis server", e)
    }
  }

}
