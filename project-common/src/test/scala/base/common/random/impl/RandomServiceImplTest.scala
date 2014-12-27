/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/24/14 4:37 PM
 */

package base.common.random.impl

import base.common.service.ServiceTest

/**
 * Tests that the service provides randomly generated unique identifiers
 * @author rconrad
 */
class RandomServiceImplTest extends ServiceTest {

  val service = new RandomServiceImpl()

  val START = 0
  val COUNT = 1000

  val MD5_LENGTH = 32
  val SHA256_LENGTH = 64
  val UUID_LENGTH = 36

  def assertUnique(f: () => String, length: Int) {
    val strings = List.range(START, COUNT).map { i =>
      val string = f()
      assert(string.length == length)
      string
    }
    assert(strings.size == strings.toSet.size)
  }

  test("md5") {
    assertUnique(() => service.md5.toString, MD5_LENGTH)
  }

  test("sha256") {
    assertUnique(() => service.sha256.toString, SHA256_LENGTH)
  }

  test("uuid") {
    assertUnique(() => service.uuid.toString, UUID_LENGTH)
  }

}
