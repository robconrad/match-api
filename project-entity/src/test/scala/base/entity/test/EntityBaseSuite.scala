/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.test

import base.common.test.BaseSuite
import base.entity.service.EntityServicesBootstrap

/**
 * Underpinnings of all test suites, ensures services have been registered at least once
 * @author rconrad
 */
trait EntityBaseSuite extends BaseSuite {

  // initialize default services (may be swapped out with mocks as-needed)
  EntityServicesBootstrap.registered

}
