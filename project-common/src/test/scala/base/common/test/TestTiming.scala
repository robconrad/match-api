/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/20/15 10:02 PM
 */

package base.common.test

import akka.util.Timeout
import org.scalatest.time.{ Seconds, Span }

import scala.concurrent.duration._

/**
 * Default options for tests that require time limits
 * @author rconrad
 */
trait TestTiming {

  implicit val defaultTimeout = new Timeout(2.seconds)
  val longTimeout = new Timeout(10.seconds)
  val probeTimeout = new Timeout(100.millis)
  val defaultSpan = Span(2, Seconds)

  implicit def timeout2Duration(t: Timeout) = t.duration

}
