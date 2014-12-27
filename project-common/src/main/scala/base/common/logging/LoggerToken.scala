/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.logging

import base.common.random.RandomService
import com.google.common.hash.HashCode

/**
 * LoggerToken is a unique identifier for a set of logs that should be associated
 *  together for some business logic purpose
 * @author rconrad
 */
case class LoggerToken(t: HashCode = RandomService().md5) {
  lazy val s = t.toString
}
