/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.test

import org.scalactic.TripleEqualsSupport.Spread
import org.scalatest.Matchers._

/**
 * Utility functions for asserting beyond Scalatest's built-in asserts
 * @author rconrad
 */
object Assertions {

  def assertDelta(actual: Double, expected: Double, delta: Double = TestConstants.DOUBLE_DELTA) {
    actual should be(Spread(expected, delta))
  }

}
