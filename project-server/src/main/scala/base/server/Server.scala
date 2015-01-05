/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 4:04 PM
 */

package base.server

import akka.util.Timeout
import base.common.lib.Dispatchable
import base.common.logging.Loggable
import base.common.service.CommonService
import base.server.service.{ ServerService, ServerServicesBootstrap }

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * App Server!
 * @author rconrad
 */
object Server extends App with Loggable with Dispatchable { PS =>

  start()

  /**
   * Setup the APIs, create hooks, etc.
   */
  def start() {
    ServerServicesBootstrap.registered

    Runtime.getRuntime.addShutdownHook(new Thread {
      override def run() {
        PS.stop()
      }
    })

    val f = ServerService().start()
    Await.result(f, 30.seconds)
  }

  /**
   * Shut down the API - any cleanup on shutdown goes here
   */
  def stop() {
    val f = ServerService().stop()
    Await.result(f, CommonService().defaultDuration)
  }

}
