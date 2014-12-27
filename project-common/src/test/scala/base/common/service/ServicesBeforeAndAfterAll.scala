/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.service

import org.scalatest.{ BeforeAndAfterAll, Suite }

/**
 * Mixin that will make sure the Services registry looks the same
 *  before and after your tests run (i.e. wherein you register some mocks)
 *  NB: if you also need to use beforeAll / afterAll, be sure to call
 *      super.beforeAll / super.afterAll within.
 * @author rconrad
 */
trait ServicesBeforeAndAfterAll extends Suite with BeforeAndAfterAll {

  private var map = Services.get

  /**
   * Record a copy of the Services registry before suite
   */
  override def beforeAll() {
    super.beforeAll()
    map = Services.get
  }

  /**
   * Re-register all services with from the clean copy
   */
  override def afterAll() {
    super.afterAll()
    map.foreach {
      case (manifest, service) =>
        Services.register(service)
    }
  }

}
