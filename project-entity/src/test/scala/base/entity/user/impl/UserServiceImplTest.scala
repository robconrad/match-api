/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/8/15 5:36 PM
 */

package base.entity.user.impl

import base.common.time.DateTimeHelper
import base.entity.service.EntityServiceTest
import org.json4s.DefaultFormats
import org.scalatest.BeforeAndAfterEach

/**
 * Responsible for testing the logic and side effects of the Impl
 *  (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class UserServiceImplTest extends EntityServiceTest with DateTimeHelper with BeforeAndAfterEach {

  val service = new UserServiceImpl

  implicit val formats = DefaultFormats

  override def afterEach() {
    super.afterEach()
  }

  ignore("create") {
    fail("not implemented")
  }

  ignore("get") {
    fail("not implemented")
  }

  ignore("update") {

  }

  ignore("resetInitiate") {

  }

  ignore("resetComplete") {

  }

}
