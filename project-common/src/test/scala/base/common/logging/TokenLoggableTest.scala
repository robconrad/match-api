/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/9/15 9:41 PM
 */

package base.common.logging

import base.common.test.BaseSuite
import base.common.test.TestExceptions.TestRuntimeException

/**
 * {{ Describe the high level purpose of LoggableTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class TokenLoggableTest extends BaseSuite with TokenLoggable {

  private val t = new TestRuntimeException("test")
  private implicit val myToken = token

  private def logAndAssert(logger: (String, String) => Unit)(implicit token: LoggerToken) {
    LoggableTestUtil.logAndAssert(logger, () => {
      assert(LoggableTestUtil.lastDefault.contains(token.s))
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
