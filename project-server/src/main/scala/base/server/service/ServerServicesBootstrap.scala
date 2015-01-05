/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 3:43 PM
 */

package base.server.service

import base.common.service.{ Services, ServicesBootstrap }
import base.server.service.impl.ServerServiceImpl

/**
 * Injects configuration into Services and boots them up. If it's configurable, it belongs here.
 * @author rconrad
 */
object ServerServicesBootstrap extends ServicesBootstrap {

  /**
   * Config Sections
   */
  private val SERVER = "server"
  private val START = Keys(SERVER, "start")

  /**
   * Registration of services, see classes for details on these parameters
   */
  lazy val registered = {

    Services.register(new ServerServiceImpl(
      Keys(START, "kv"),
      Keys(START, "db"),
      Keys(START, "rest"),
      Keys(START, "socket")))

    true
  }

}
