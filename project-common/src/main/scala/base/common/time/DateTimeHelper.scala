/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.time

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

import scala.concurrent.duration.FiniteDuration

/**
 * Helper mixin that provides commonly used time manipulation functions
 * @author rconrad
 */
trait DateTimeHelper {

  implicit def dateTime2String(d: DateTime) = d.toString(ISODateTimeFormat.dateTime())
  implicit def duration2Millis(d: FiniteDuration) = d.toMillis

  def now: DateTime = TimeService().now
  def now(millis: Long): DateTime = now.plus(millis)

  val MILLIS_100 = 100L
  val MILLIS_1000 = 1000L
  val MILLIS_10000 = 10000L

}
