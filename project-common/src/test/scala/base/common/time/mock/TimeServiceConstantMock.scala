/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 12:33 PM
 */

package base.common.time.mock

import base.common.time.TimeService
import base.common.time.impl.TimeServiceImpl
import org.joda.time.DateTime

/**
 * This time service will hold time constant for testing purposes
 * @author rconrad
 */
object TimeServiceConstantMock extends TimeService {

  val now = DateTime.now()

  def asString(d: DateTime) = TimeServiceImpl.asString(d)

  def fromString(s: String) = TimeServiceImpl.fromString(s)

}
