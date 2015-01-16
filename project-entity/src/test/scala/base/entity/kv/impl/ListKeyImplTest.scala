/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 10:28 PM
 */

package base.entity.kv.impl

import base.entity.kv.mock.KeyLoggerMock

/**
 * {{ Describe the high level purpose of IntModelTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class ListKeyImplTest extends KeyImplTest {

  private val val1 = "value1"
  private val val2 = "value2"

  val model = new ListKeyImpl {
    val token = this.getClass.getSimpleName
    val logger = KeyLoggerMock
  }

  def create = model.prepend(val1).await()

  test("prepend") {
    assert(model.prepend(val1).await())
    assert(model.prepend(val1, val2).await())
  }

  test("prependIfExists") {
    assert(!model.prependIfExists(val1).await())
    assert(model.prepend(val1).await())
    assert(model.prependIfExists(val1).await())
  }

}
