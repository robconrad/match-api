/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.rest.test

import base.common.service.CommonServicesBootstrap
import base.common.test.BaseSuite
import base.entity.service.EntityServicesBootstrap
import base.rest.service.RestServicesBootstrap

/**
 * Underpinnings of all test suites, ensures services have been registered at least once
 * @author rconrad
 */
private[rest] trait RestBaseSuite extends BaseSuite {

  // initialize default services (may be swapped out with mocks as-needed)
  CommonServicesBootstrap.registered
  EntityServicesBootstrap.registered
  RestServicesBootstrap.registered

}
