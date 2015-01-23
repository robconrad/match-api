/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 3:16 PM
 */

package base.entity.kv.impl

import base.entity.kv.mock.KeyLoggerMock

/**
 * {{ Describe the high level purpose of IntModelTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class IntKeyImplTest extends KeyImplTest {

  private val int = 10

  val model = new IntKeyImpl {
    val token = this.getClass.getSimpleName.getBytes
    val logger = KeyLoggerMock
    protected implicit val p = tp
  }

  def create = model.set(int).await()

  test("incr") {
    assert(model.get().await() == None)
    assert(model.increment().await() == 1)
    assert(model.increment().await() == 2)
  }

  test("get / set") {
    assert(model.get().await() == None)
    assert(model.set(int).await())
    assert(model.get().await() == Option(int))
  }

}
