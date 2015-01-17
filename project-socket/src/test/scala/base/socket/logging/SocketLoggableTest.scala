/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 1:50 PM
 */

package base.socket.logging

import base.common.logging.LoggableTestUtil
import base.entity.auth.context.NoAuthContext
import base.entity.test.EntityBaseSuite

/**
 * Test the AuthLoggable context imprint extension to the base logger to ensure it properly adds the
 *  identifying AuthContext prefix to all log levels
 * @author rconrad
 */
class SocketLoggableTest extends EntityBaseSuite with SocketLoggable {

  private implicit val ch = new LoggableChannelInfo {
    def authCtx = NoAuthContext
    def remoteAddress = "123 fake street"
  }
  private val t = new RuntimeException("error!")

  private def logAndAssert(logger: (String, String) => Unit)(implicit ch: LoggableChannelInfo) {
    LoggableTestUtil.logAndAssert(logger, () => {
      assert(LoggableTestUtil.lastDefault.contains(ch.remoteAddress))
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
