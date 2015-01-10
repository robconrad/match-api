/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/9/15 9:38 PM
 */

package base.common.logging

import base.common.logging.LoggableTestUtil._
import base.common.test.BaseSuite
import base.common.test.TestExceptions.TestRuntimeException

/**
 * {{ Describe the high level purpose of LoggableTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class LoggableTest extends BaseSuite with Loggable {

  private val t = new TestRuntimeException("test")

  test("loggerName") {
    assert(loggerName == this.getClass.getSimpleName)
  }

  test("isEnabled") {
    assert(isTraceEnabled)
    assert(isDebugEnabled)
    assert(isInfoEnabled)
    assert(isWarnEnabled)
    assert(isErrorEnabled)
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
