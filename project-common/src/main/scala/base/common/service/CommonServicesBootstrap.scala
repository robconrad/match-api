/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.service

import base.common.random.impl.RandomServiceImpl
import base.common.server.impl.ServerServiceImpl
import base.common.time.impl.TimeServiceImpl

/**
 * Injects configuration into Services and boots them up. If it's configurable, it belongs here.
 * @author rconrad
 */
object CommonServicesBootstrap extends ServicesBootstrap {

  /**
   * Trigger and status indicator for executing bootstrap startup behavior (i.e. registering services)
   */
  lazy val registered = {

    Services.register(new RandomServiceImpl())

    Services.register(new ServerServiceImpl(
      Keys(AKKA, "host"),
      Keys(AKKA, "port"),
      Keys(AKKA, "defaultTimeout")
    ))

    Services.register(new TimeServiceImpl())

    true
  }

}
