/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 2:46 PM
 */

package base.common.test

import base.common.service.CommonServicesBootstrap
import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuite

/**
 * Underpinnings of all test suites, ensures services have been registered at least once
 * @author rconrad
 */
trait BaseSuite extends FunSuite with PimpMyFutures with MockFactory {

  // initialize default services (may be swapped out with mocks as-needed)
  CommonServicesBootstrap.registered

}
