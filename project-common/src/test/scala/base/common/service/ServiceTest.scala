/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.service

import base.common.lib.Dispatchable
import base.common.logging.Loggable
import base.common.test.BaseSuite

/**
 * Base service test class, sets up other services etc.
 * @author rconrad
 */
abstract class ServiceTest extends BaseSuite with Dispatchable with ServicesBeforeAndAfterAll with Loggable {

  val service: Service

  override def beforeAll() {
    super.beforeAll()
    Services.register(service)
  }

}
