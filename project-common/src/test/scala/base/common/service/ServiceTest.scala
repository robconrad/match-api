/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 11:37 AM
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

  def service: Service

  override def beforeAll() {
    super.beforeAll()
    Services.register(service)
  }

}
