/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/3/15 2:04 PM
 */

package base.entity.kv.impl

import base.entity.kv.mock.KeyLoggerMock

/**
 * {{ Describe the high level purpose of IntModelTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class SetKeyImplTest extends KeyImplTest {

  private val val1 = "value1"
  private val val2 = "value2"

  val model = new SetKeyImpl(getClass.getSimpleName, KeyLoggerMock)

  def create = model.add(val1).await()

  test("members") {
    assert(model.members().await() == Set())
    assert(model.add(val1).await())
    assert(model.members().await() == Set(val1))
    assert(model.add(val2).await())
    assert(model.members().await() == Set(val1, val2))
  }

  test("isMember") {
    assert(!model.isMember(val1).await())
    assert(model.add(val1).await())
    assert(model.isMember(val1).await())
  }

  test("rand") {
    assert(model.rand().await() == None)
    assert(model.add(val1).await())
    assert(model.rand().await() == Option(val1))
  }

  test("pop") {
    assert(model.pop().await() == None)
    assert(model.add(val1).await())
    assert(model.pop().await() == Option(val1))
    assert(model.pop().await() == None)
  }

  test("remove") {
    assert(!model.remove(val1).await())
    assert(model.add(val1).await())
    assert(model.remove(val1).await())
  }

  test("move") {
    val dest = new SetKeyImpl("destination", KeyLoggerMock)
    assert(model.add(val1).await())
    assert(model.move(dest, val1).await())
    assert(dest.members().await() == Set(val1))
    assert(model.members().await() == Set())
  }

}
