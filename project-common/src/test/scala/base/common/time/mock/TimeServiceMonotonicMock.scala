/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.time.mock

import base.common.time.TimeService
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

  def nowNoUpdate = dateTime

}
