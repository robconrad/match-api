/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/7/15 3:40 PM
 */

package base.entity.kv.impl

import base.common.service.{ CommonService, ServiceImpl }
import base.entity.kv._
import scredis.Redis

import scala.concurrent.Await
import scala.util.Random

/**
 * {{ Describe the high level purpose of KvServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class ScredisFactoryServiceImpl(clientCount: Int, host: String, port: Int)
    extends ServiceImpl
    with ScredisFactoryService {

  private val PING_RESPONSE = "PONG"
  private val random = new Random()

  private lazy val clients = List.fill(clientCount)(makeClient())

  private def client = clients(random.nextInt(clientCount))

  def hashCommands = client
  def keyCommands = client
  def listCommands = client
  def setCommands = client
  def sortedSetCommands = client
  def stringCommands = client

  private def makeClient() = {
    try {
      val redis = Redis(host = host, port = port)
      val ping = Await.result(redis.ping(), CommonService().defaultDuration)

      if (ping != PING_RESPONSE) {
        throw new Exception("Pinging redis server returned unexpected response " + ping)
      }

      redis
    } catch {
      case e: Exception => throw new Exception("Unable to connect to and ping redis server", e)
    }
  }

}
