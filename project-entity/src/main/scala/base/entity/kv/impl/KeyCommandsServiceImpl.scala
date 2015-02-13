/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/12/15 8:52 PM
 */

package base.entity.kv.impl

import base.common.service.{ CommonService, ServiceImpl }
import base.entity.kv._
import scredis.Client

import scala.concurrent.Await
import scala.util.Random

/**
 * {{ Describe the high level purpose of KvServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class KeyCommandsServiceImpl(clientCount: Int, host: String, port: Int)
    extends ServiceImpl
    with KeyCommandsService {

  private val PING_RESPONSE = "PONG"
  private val random = new Random()

  private lazy val clients = List.fill(clientCount)(makeClient())

  def client = clients(random.nextInt(clientCount))

  private def makeClient() = {
    try {
      val client = Client(host = host, port = port)
      val ping = Await.result(client.ping(), CommonService().defaultDuration)

      if (ping != PING_RESPONSE) {
        throw new Exception("Pinging redis server returned unexpected response " + ping)
      }

      client
    } catch {
      case e: Exception => throw new Exception("Unable to connect to and ping redis server", e)
    }
  }

}
