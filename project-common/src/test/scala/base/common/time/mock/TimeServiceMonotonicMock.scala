/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/15/15 9:10 PM
 */

package base.common.time.mock

import base.common.time.TimeService
import base.common.time.impl.TimeServiceImpl
import org.joda.time.DateTime

/**
 * This time service will increment time by one second for each instance it is asked
 * @author rconrad
 */
object TimeServiceMonotonicMock extends TimeService {

  private var dateTime = DateTime.now()

  def now = {
    dateTime = dateTime.plusSeconds(1)
    dateTime
  }

  def asString(d: DateTime) = TimeServiceImpl.asString(d)

  def fromString(s: String) = TimeServiceImpl.fromString(s)

  def nowNoUpdate = dateTime

}
