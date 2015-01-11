/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 9:56 AM
 */

package base.entity.user.impl

import base.common.lib.Genders
import base.common.random.RandomService
import base.entity.api.ApiVersions
import base.entity.kv.KvTest
import base.entity.service.EntityServiceTest
import base.entity.user.model.VerifyModel

/**
 * Responsible for testing the logic and side effects of the Impl
 * (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class VerifyServiceImplTest extends EntityServiceTest with KvTest {

  val service = new VerifyServiceImpl

  test("without perms") {
    val model = VerifyModel(ApiVersions.V01, "Bob", Genders.male, "1234", RandomService().uuid, "code")
    assertPermException(authCtx => {
      service.verify(model)(authCtx)
    })
  }

}
