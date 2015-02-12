/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 9:35 PM
 */

package base.entity.kv.impl

import base.entity.kv.KeyPrefixes

/**
 * {{ Describe the high level purpose of IntModelTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class IntKeyImplTest extends KeyImplTest {

  private val int = 10

  val model = new SimpleKeyImpl[String, Int] {
    def keyPrefix = KeyPrefixes.test
    def keyValue = "test"
  }

  def create = model.set(int).await()

  test("incr") {
    assert(model.get.await() == None)
    assert(model.incr.await() == 1)
    assert(model.incr.await() == 2)
  }

  test("get / set") {
    assert(model.get.await() == None)
    assert(model.set(int).await())
    assert(model.get.await() == Option(int))
  }

}
