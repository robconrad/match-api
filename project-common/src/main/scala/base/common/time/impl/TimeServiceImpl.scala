/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.time.impl

import base.common.service.ServiceImpl
import base.common.time.TimeService
import org.joda.time.DateTime

/**
 * Provides time functions that can be mocked out
 * @author rconrad
 */
class TimeServiceImpl extends ServiceImpl with TimeService {

  def now = DateTime.now()

}
