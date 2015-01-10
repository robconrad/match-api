/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/9/15 9:37 PM
 */

package base.common.logging

import base.common.random.RandomService
import base.common.time.TimeService
import org.joda.time.format.ISODateTimeFormat

import scala.io.Source

/**
 * {{ Describe the high level purpose of LoggableTestUtil here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
object LoggableTestUtil {

  // extraordinarily inefficient but it's scanning a test log so.. meh.
  def lastDefault = Source.fromFile("log/default.log").getLines().toList
    .filter(_.contains(TimeService().now.toString(ISODateTimeFormat.yearMonthDay()))).last

  def logAndAssert(logger: (String, String) => Unit, f: () => Unit = () => Unit) {
    val arg = RandomService().sha256.toString
    logger("echo %s", arg)
    assert(lastDefault.contains(arg))
    f()
  }

}
