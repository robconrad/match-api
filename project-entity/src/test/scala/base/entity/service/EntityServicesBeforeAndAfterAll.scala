/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.service

import base.common.service.ServicesBeforeAndAfterAll
import base.entity.db.DbTestHelper

/**
 * Mixin that will make sure the Services registry looks the same
 *  before and after your tests run (i.e. wherein you register some mocks)
 *  NB: if you also need to use beforeAll / afterAll, be sure to call
 *      super.beforeAll / super.afterAll within.
 * @author rconrad
 */
private[entity] trait EntityServicesBeforeAndAfterAll extends ServicesBeforeAndAfterAll with DbTestHelper {

  protected def shouldSetupAndCleanDb = false
  protected def shouldSetupDb = shouldSetupAndCleanDb
  protected def shouldCleanDb = shouldSetupAndCleanDb

  /**
   * Record a copy of the Services registry before suite
   *  Optionally setup the database
   */
  override def beforeAll() {
    super.beforeAll()

    if (shouldSetupDb) {
      setupDb()
    }
  }

  /**
   * Re-register all services with from the clean copy
   *  Optionally clear the database
   */
  override def afterAll() {
    super.afterAll()

    if (shouldCleanDb) {
      cleanDb()
    }
  }

}
