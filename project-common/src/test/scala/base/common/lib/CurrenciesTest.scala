/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/4/15 6:19 PM
 */

package base.common.lib

import base.common.lib.Currencies._
import base.common.test.BaseSuite

/**
 * Tests the Currencies enum functions for expected values
 * @author rconrad
 */
class CurrenciesTest extends BaseSuite {

  /**
   * Super important that this be correct since it is part of the security hash. Customers
   *  have to ber able to replicate this behavior exactly to come up with the right hash.
   */
  test("withPrecision USD") {

    assert("1.10" == preciselyString(USD, 1.1d))
    assert("1.10" == preciselyString(USD, 1.10d))
    assert("1.10" == preciselyString(USD, 1.101d))
    assert("1.10" == preciselyString(USD, 1.1045d))
    assert("1.11" == preciselyString(USD, 1.106d))
    assert("111111.11" == preciselyString(USD, 111111.109d))
    assert(111111.11d == preciselyDouble(USD, 111111.109d))

    assert("0.000000000" == preciselyString(BTC, 0.0000000004d))
    assert("0.000000001" == preciselyString(BTC, 0.0000000009d))
    assert("0.000000009" == preciselyString(BTC, 0.000000009d))
    assert("0.000900009" == preciselyString(BTC, 0.000900009d))
    assert("1.110000000" == preciselyString(BTC, 1.11d))
    assert("1111111.110000000" == preciselyString(BTC, 1111111.11d))
    assert("1111111.000000001" == preciselyString(BTC, 1111111.0000000009d))
    assert(1111111.000000001d == preciselyDouble(BTC, 1111111.0000000009d))

  }

  test("allowedDelta") {
    assert(Currencies.allowedDelta(USD) == 0.01d)
    assert(Currencies.allowedDelta(KRW) == 1.0d)
  }

  test("asString / asValue") {
    val s: String = USD
    assert(s == "USD")
    val c: Currency = "USD"
    assert(c == USD)
  }

}
