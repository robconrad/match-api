/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 3:58 PM
 */

package base.common.service

import base.common.random.impl.RandomServiceImpl
import base.common.service.impl.CommonServiceImpl
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

    Services.register(new CommonServiceImpl(
      Keys(AKKA, "host"),
      Keys(AKKA, "port"),
      Keys(AKKA, "defaultTimeout")
    ))

    Services.register(new TimeServiceImpl())

    true
  }

}
