/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 9:38 PM
 */

package base.entity.kv.impl

import base.entity.kv.KeyPrefixes

/**
 * {{ Describe the high level purpose of IntModelTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class ListKeyImplTest extends KeyImplTest {

  private val val1 = "value1"
  private val val2 = "value2"

  val model = new ListKeyImpl[String, String] {
    def keyPrefix = KeyPrefixes.test
    def keyValue = "test"
  }

  def create = model.lPush(val1).await() == 1L

  test("prepend") {
    assert(model.lPush(val1).await() == 1L)
    assert(model.lPush(val1, val2).await() == 3L)
  }

  test("prependIfExists") {
    assert(model.lPushX(val1).await() == 0L)
    assert(model.lPush(val1).await() == 1L)
    assert(model.lPushX(val1).await() == 2L)
  }

}
