/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 10:04 PM
 */

package base.entity.auth.impl

import base.entity.service.EntityServiceTest

/**
 * Responsible for testing the logic and side effects of the Impl
 *  (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class AuthServiceImplTest() extends EntityServiceTest {

  override protected val shouldSetupAndCleanDb = true

  val service = new AuthServiceImpl()

}
