/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.rest.service

import base.common.service.{ Services, ServicesBootstrap }
import base.rest.server.impl.RestServerServiceImpl

/**
 * Injects configuration into Services and boots them up. If it's configurable, it belongs here.
 * @author rconrad
 */
private[rest] object RestServicesBootstrap extends ServicesBootstrap {

  /**
   * Config Sections
   */
  private val REST = "rest"

  /**
   * Registration of services, see classes for details on these parameters
   */
  lazy val registered = {

    Services.register(new RestServerServiceImpl(
      Keys(REST, "protocol"),
      Keys(REST, "host"),
      Keys(REST, "port")))

    true
  }

}
