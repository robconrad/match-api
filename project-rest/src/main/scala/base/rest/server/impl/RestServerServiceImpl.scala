/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.rest.server.impl

import akka.actor.Props
import akka.io.IO
import akka.pattern.ask
import base.common.lib.Actors
import base.common.server.ServerService
import base.common.service.ServiceImpl
import base.rest.route.RoutingActor
import base.rest.server.RestServerService
import spray.can.Http

/**
 * Responsible for configuring, starting, binding, etc. the REST API
 * @author rconrad
 */
private[rest] class RestServerServiceImpl(val protocol: String,
                                          val host: String,
                                          val port: Int) extends ServiceImpl with RestServerService {

  implicit val defaultTimeout = ServerService().defaultTimeout

  def url = s"$protocol://$host:$port/"

  /**
   * Start the REST server and bind to the configured port
   */
  def start() = {
    val actor = Actors.actorSystem.actorOf(Props[RoutingActor], "rest-service")
    IO(Http)(Actors.actorSystem) ? Http.Bind(actor, host, port) map { r =>
      info(s"REST API has started ($r)")
    }
  }

}
