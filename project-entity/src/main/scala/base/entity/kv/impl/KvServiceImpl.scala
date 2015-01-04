/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/3/15 1:02 PM
 */

package base.entity.kv.impl

import base.common.service.ServiceImpl
import base.entity.kv.KvService
import redis.client.RedisClient

import scala.util.Random

/**
 * {{ Describe the high level purpose of KvServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class KvServiceImpl(clientCount: Int, host: String, port: Int) extends ServiceImpl with KvService {

  private val PING_RESPONSE = "PONG"
  private val random = new Random()

  private lazy val clients = List.fill(clientCount)(makeClient())

  def client = clients(random.nextInt(clientCount))

  def pipeline = client.pipeline()

  def makeHashKeyFactory(ch: KeyChannel) = new HashKeyFactoryImpl(ch)
  def makeIntKeyFactory(ch: KeyChannel) = new IntKeyFactoryImpl(ch)
  def makeSetKeyFactory(ch: KeyChannel) = new SetKeyFactoryImpl(ch)

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
