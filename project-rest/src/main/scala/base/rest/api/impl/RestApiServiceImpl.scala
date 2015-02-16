/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:10 PM
 */

package base.rest.api.impl

import akka.actor.Props
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import base.common.lib.Actors
import base.common.service.ServiceImpl
import base.rest.api.RestApiService
import base.rest.route.RoutingActor
import spray.can.Http

import scala.concurrent.duration._

/**
 * Responsible for configuring, starting, binding, etc. the REST API
 * @author rconrad
 */
private[rest] class RestApiServiceImpl(val protocol: String,
                                       val host: String,
                                       val port: Int) extends ServiceImpl with RestApiService {

  implicit val defaultTimeout = new Timeout(30.seconds)

  def url = s"$protocol://$host:$port/"

  /**
   * Start the REST server and bind to the configured port
   */
  def start() = {
    // Bring up the REST API and bind it
    val actor = Actors.actorSystem.actorOf(Props[RoutingActor], "rest-service")
    IO(Http)(Actors.actorSystem) ? Http.Bind(actor, host, port) map { r =>
      info(s"REST API has started ($r)")
      true
    }
  }

  def stop() = {
    IO(Http)(Actors.actorSystem) ? Http.Unbind map { r =>
      info(s"REST API has stopped ($r)")
      true
    }
  }

}
