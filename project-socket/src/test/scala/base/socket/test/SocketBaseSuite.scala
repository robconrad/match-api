/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 4:16 PM
 */

package base.socket.test

import base.common.service.CommonServicesBootstrap
import base.common.test.BaseSuite
import base.entity.service.EntityServicesBootstrap
import base.socket.service.SocketServicesBootstrap

/**
 * Underpinnings of all test suites, ensures services have been registered at least once
 * @author rconrad
 */
private[socket] trait SocketBaseSuite extends BaseSuite {

  // initialize default services (may be swapped out with mocks as-needed)
  CommonServicesBootstrap.registered
  EntityServicesBootstrap.registered
  SocketServicesBootstrap.registered

}
