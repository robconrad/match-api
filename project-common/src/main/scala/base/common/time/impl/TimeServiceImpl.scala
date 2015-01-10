/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/9/15 8:52 PM
 */

package base.common.time.impl

import base.common.service.ServiceImpl
import base.common.time.TimeService
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

/**
 * Provides time functions that can be mocked out
 * @author rconrad
 */
class TimeServiceImpl extends ServiceImpl with TimeService {

  def now = DateTime.now()

  def asString(d: DateTime) = TimeServiceImpl.asString(d)

}

object TimeServiceImpl {

  def asString(d: DateTime) = d.toString(ISODateTimeFormat.dateTime())

}
