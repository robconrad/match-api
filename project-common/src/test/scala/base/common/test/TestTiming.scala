/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.test

import akka.util.Timeout

import scala.concurrent.duration._

/**
 * Default options for tests that require time limits
 * @author rconrad
 */
trait TestTiming {

  implicit val timeout = new Timeout(2.seconds)
  val longTimeout = new Timeout(10.seconds)
  val probeTimeout = new Timeout(100.millis)

  implicit def timeout2Duration(t: Timeout) = t.duration

}
