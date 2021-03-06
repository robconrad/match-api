/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 4:12 PM
 */

package base.rest.service

import base.common.service.{ Services, ServicesBootstrap }
import base.rest.api.impl.RestApiServiceImpl

/**
 * Injects configuration into Services and boots them up. If it's configurable, it belongs here.
 * @author rconrad
 */
object RestServicesBootstrap extends ServicesBootstrap {

  /**
   * Config Sections
   */
  private val REST = "rest"

  /**
   * Registration of services, see classes for details on these parameters
   */
  lazy val registered = {

    Services.register(new RestApiServiceImpl(
      Keys(REST, "protocol"),
      Keys(REST, "host"),
      Keys(REST, "port")))

    true
  }

}
