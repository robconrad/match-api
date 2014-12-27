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
 * This time service will hold time constant for testing purposes
 * @author rconrad
 */
object TimeServiceConstantMock extends TimeService {

  val now = DateTime.now()

}
