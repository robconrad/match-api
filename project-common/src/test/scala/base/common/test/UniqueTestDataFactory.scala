/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.test

import java.util.concurrent.atomic.{ AtomicInteger, AtomicLong }

/**
 * Produces test data that is unique within this run of the app
 * @author rconrad
 */
object UniqueTestDataFactory {

  private val stringCount = new AtomicLong(0L)
  private val intCount = new AtomicInteger(0)
  private val longCount = new AtomicLong(0L)

  def string: String = {
    string()
  }

  def string(prefix: String = "testString"): String = {
    prefix + stringCount.incrementAndGet()
  }

  def int = {
    intCount.incrementAndGet()
  }

  def long = {
    longCount.incrementAndGet()
  }

}
