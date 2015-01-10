/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 10:09 AM
 */

package base.entity.logging

import base.common.logging._
import base.entity.auth.context.{ AuthContext, AuthContextDataFactory }
import base.entity.perm.PermSetGroups
import base.entity.test.EntityBaseSuite
import base.entity.user.User

/**
 * Test the AuthLoggable context imprint extension to the base logger to ensure it properly adds the
 *  identifying AuthContext prefix to all log levels
 * @author rconrad
 */
class AuthLoggableTest extends EntityBaseSuite with AuthLoggable {

  private implicit val authCtx = AuthContextDataFactory.userAuth
  private val t = new RuntimeException("error!")

  private def logAndAssert(logger: (String, String) => Unit)(implicit authCtx: AuthContext) {
    LoggableTestUtil.logAndAssert(logger, () => {
      assert(LoggableTestUtil.lastDefault.contains(authCtx.toLogPrefix))
    })
  }

  test("trace") {
    logAndAssert((msg, arg) => trace(msg, arg))
  }

  test("debug") {
    logAndAssert((msg, arg) => debug(msg, arg))
  }

  test("info") {
    logAndAssert((msg, arg) => info(msg, arg))
  }

  test("warn") {
    logAndAssert((msg, arg) => warn(msg, arg))
    logAndAssert((msg, arg) => warn(msg, t, arg))
  }

  test("error") {
    logAndAssert((msg, arg) => error(msg, arg))
    logAndAssert((msg, arg) => error(msg, t, arg))
  }

}
