/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 9:54 PM
 */

package base.entity.logging

import base.common.logging._
import base.entity.auth.context.{ ChannelContext, ChannelContextDataFactory }
import base.entity.test.EntityBaseSuite

/**
 * Test the AuthLoggable context imprint extension to the base logger to ensure it properly adds the
 *  identifying AuthContext prefix to all log levels
 * @author rconrad
 */
class AuthLoggableTest extends EntityBaseSuite with AuthLoggable {

  private implicit val channelCtx = ChannelContextDataFactory.userAuth
  private val t = new RuntimeException("error!")

  private def logAndAssert(logger: (String, String) => Unit)(implicit channelCtx: ChannelContext) {
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
