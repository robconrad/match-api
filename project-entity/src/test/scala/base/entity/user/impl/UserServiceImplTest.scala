/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 2:56 PM
 */

package base.entity.user.impl

import base.common.lib.Genders
import base.common.random.RandomService
import base.entity.api.ApiVersions
import base.entity.auth.context.{ AuthContext, AuthContextDataFactory }
import base.entity.device.model.DeviceModel
import base.entity.perm.PermException
import base.entity.service.EntityServiceTest
import base.entity.user.model.{ VerifyModel, LoginModel, RegisterModel }
import org.json4s.DefaultFormats
import org.scalatest.BeforeAndAfterEach

/**
 * Responsible for testing the logic and side effects of the Impl
 *  (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class UserServiceImplTest extends EntityServiceTest with BeforeAndAfterEach {

  val service = new UserServiceImpl

  implicit val formats = DefaultFormats

  override def afterEach() {
    super.afterEach()
  }

  private def assertPerm(f: AuthContext => Unit) {
    intercept[PermException] {
      f(AuthContextDataFactory.emptyUserAuth)
    }
  }

  test("register without perms") {
    val model = RegisterModel(ApiVersions.V01, "bob", Genders.male, "1234")
    assertPerm(authCtx => {
      service.register(model)(authCtx)
    })
  }

  test("verify without perms") {
    val model = VerifyModel(ApiVersions.V01, "1234", RandomService().uuid, "code")
    assertPerm(authCtx => {
      service.verify(model)(authCtx)
    })
  }

  test("login without perms") {
    val model = LoginModel(RandomService().uuid, None, "app version", ApiVersions.V01, "EN",
      DeviceModel(RandomService().uuid, "model", "cordova", "platform", "version", "name"))
    assertPerm(authCtx => {
      service.login(model)(authCtx)
    })
  }

  test("register") {

  }

}
