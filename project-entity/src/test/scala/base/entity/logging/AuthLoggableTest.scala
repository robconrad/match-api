/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.entity.logging

import base.common.random.RandomService
import base.common.time.DateTimeHelper
import base.entity.auth.context.{ AuthContext, AuthContextDataFactory }
import base.entity.test.EntityBaseSuite
import org.joda.time.format.ISODateTimeFormat

import scala.io.Source

/**
 * Test the AuthLoggable context imprint extension to the base logger to ensure it properly adds the
 *  identifying AuthContext prefix to all log levels
 * @author rconrad
 */
class AuthLoggableTest extends EntityBaseSuite with AuthLoggable with DateTimeHelper {

  private implicit val authCtx = AuthContextDataFactory.emptyUserAuth
  private val t = new RuntimeException("error!")

  // extraordinarily inefficient but it's scanning a test log so.. meh.
  private def lastDefault = Source.fromFile("log/default.log").getLines().toList
    .filter(_.contains(now.toString(ISODateTimeFormat.yearMonthDay()))).last

  private def logAndAssert(logger: (String, String) => Unit)(implicit authCtx: AuthContext) {
    val arg = RandomService().sha256.toString
    logger("echo %s", arg)
    assert(lastDefault.contains(arg))
    assert(lastDefault.contains(authCtx.toLogPrefix))
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
