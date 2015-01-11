/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 9:56 AM
 */

package base.entity.user.impl

import base.common.random.RandomService
import base.entity.api.ApiVersions
import base.entity.device.model.DeviceModel
import base.entity.kv.KvTest
import base.entity.service.EntityServiceTest
import base.entity.user.model.LoginModel

/**
 * Responsible for testing the logic and side effects of the Impl
 *  (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class LoginServiceImplTest extends EntityServiceTest with KvTest {

  val service = new LoginServiceImpl

  test("without perms") {
    val model = LoginModel(RandomService().uuid, None, "app version", ApiVersions.V01, "EN",
      DeviceModel(RandomService().uuid, "model", "cordova", "platform", "version", "name"))
    assertPermException(authCtx => {
      service.login(model)(authCtx)
    })
  }

}
