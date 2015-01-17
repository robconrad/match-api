/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 1:19 PM
 */

package base.socket.service

import base.common.service.{ Services, ServicesBootstrap }
import base.socket.api.impl.{ SocketApiHandlerServiceImpl, SocketApiServiceImpl, SocketApiStatsServiceImpl }
import base.socket.command.impl.CommandProcessingServiceImpl

/**
 * Injects configuration into Services and boots them up. If it's configurable, it belongs here.
 * @author rconrad
 */
object SocketServicesBootstrap extends ServicesBootstrap {

  /**
   * Config Sections
   */
  private val SOCKET = "socket"

  /**
   * Registration of services, see classes for details on these parameters
   */
  lazy val registered = {

    Services.register(new SocketApiServiceImpl(
      Keys(SOCKET, "host"),
      Keys(SOCKET, "port"),
      Keys(SOCKET, "connectionsAllowed"),
      Keys(SOCKET, "stopSleep"),
      Keys(SOCKET, "shutdownTimeout")))

    Services.register(new SocketApiHandlerServiceImpl())

    Services.register(new SocketApiStatsServiceImpl(
      Keys(SOCKET, "reportingInterval")))

    Services.register(new CommandProcessingServiceImpl())

    true
  }

}
