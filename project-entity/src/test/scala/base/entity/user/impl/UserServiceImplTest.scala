/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 5:56 PM
 */

package base.entity.user.impl

import base.common.time.DateTimeHelper
import base.entity.service.EntityServiceTest
import base.entity.user.UserDataFactory
import org.json4s.DefaultFormats
import org.scalatest.BeforeAndAfterEach

/**
 * Responsible for testing the logic and side effects of the Impl
 *  (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class UserServiceImplTest extends EntityServiceTest with DateTimeHelper with BeforeAndAfterEach {

  override protected val shouldSetupAndCleanDb = true

  val service = new UserServiceImpl

  implicit val formats = DefaultFormats

  override def afterEach() {
    super.afterEach()
    UserDataFactory.cleanup()
  }

  test("create") {
    fail("not implemented")
  }

  test("get") {
    fail("not implemented")
  }

  test("update") {

  }

  test("resetInitiate") {

  }

  test("resetComplete") {

  }

}
