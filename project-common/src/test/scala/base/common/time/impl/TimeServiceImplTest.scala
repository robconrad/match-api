/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/9/15 9:05 PM
 */

package base.common.time.impl

import base.common.test.BaseSuite
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

/**
 * Tests that the time service is implemented correctly..
 * @author rconrad
 */
class TimeServiceImplTest extends BaseSuite {

  val service = new TimeServiceImpl

  test("now") {
    val serviceTime = service.now
    Thread.sleep(1)
    val jodaTime = DateTime.now()
    assert(serviceTime.isBefore(jodaTime))
  }

  test("asString") {
    val d = DateTime.now()
    assert(service.asString(d) == d.toString(ISODateTimeFormat.dateTime()))
    assert(TimeServiceImpl.asString(d) == service.asString(d))
  }

}
