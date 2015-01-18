/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 12:43 PM
 */

package base.common.test

import org.scalatest.Matchers._
import org.scalautils.TripleEqualsSupport.Spread

/**
 * Utility functions for asserting beyond Scalatest's built-in asserts
 * @author rconrad
 */
object Assertions {

  def assertDelta(actual: Double, expected: Double, delta: Double = TestConstants.DOUBLE_DELTA) {
    actual should be(Spread(expected, delta))
  }

}
