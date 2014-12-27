/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/25/14 11:28 AM
 */

package base.entity.apiKey.impl

import base.common.random.mock.RandomServiceMock
import base.common.service.Services
import base.common.time.DateTimeHelper
import base.common.time.mock.TimeServiceMonotonicMock
import base.entity.apiKey._
import base.entity.auth.context._
import base.entity.service.EntityServiceTest
import org.json4s.DefaultFormats
import org.scalatest.BeforeAndAfterEach

/**
 * Responsible for testing the logic and side effects of the Impl
 *  (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class ApiKeysServiceImplTest extends EntityServiceTest with DateTimeHelper with BeforeAndAfterEach {

  override protected val shouldSetupAndCleanDb = true

  val service = new ApiKeysServiceImpl

  implicit val formats = DefaultFormats

  private val random = new RandomServiceMock()

  private implicit var siteAuthCtx: AuthContext = _

  override def beforeEach() {
    super.beforeEach()

    Services.register(TimeServiceMonotonicMock)
    Services.register(random)

    ApiKeyDataFactory.cleanup()
  }

  override def afterAll() {
    super.afterAll()
    ApiKeyDataFactory.cleanup()
  }

  test("there are no tests asshole") {
    fail("none")
  }

}
