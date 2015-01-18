/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:43 PM
 */

package base.entity.kv.impl

import base.entity.kv.KeyLogger
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

  val model = new SetKeyImpl {
    val token = this.getClass.getSimpleName
    val logger = KeyLoggerMock
    protected implicit val p = tp
  }

  def create = model.add(val1).await() == 1

  test("members") {
    assert(model.members().await() == Set())
    assert(model.add(val1).await() == 1)
    assert(model.members().await() == Set(val1))
    assert(model.add(val2).await() == 1)
    assert(model.members().await() == Set(val1, val2))
  }

  test("isMember") {
    assert(!model.isMember(val1).await())
    assert(model.add(val1).await() == 1)
    assert(model.isMember(val1).await())
  }

  test("rand") {
    assert(model.rand().await() == None)
    assert(model.add(val1).await() == 1)
    assert(model.rand().await() == Option(val1))
  }

  test("rand(count)") {
    val count = 3
    assert(model.rand(count).await() == Set())
    assert(model.add(val1, val2).await() == 2)
    assert(model.rand(count).await() == Set(val1, val2))
  }

  test("pop") {
    assert(model.pop().await() == None)
    assert(model.add(val1).await() == 1)
    assert(model.pop().await() == Option(val1))
    assert(model.pop().await() == None)
  }

  test("add") {
    assert(model.add(val1).await() == 1)
    assert(model.add(val1).await() == 0)
    assert(model.add(val1, val2).await() == 1)
  }

  test("remove") {
    assert(!model.remove(val1).await())
    assert(model.add(val1).await() == 1)
    assert(model.remove(val1).await())
  }

  test("move") {
    val dest = new SetKeyImpl {
      val token = this.getClass.getSimpleName + "destination"
      val logger = KeyLoggerMock
      protected implicit val p = tp
    }
    assert(model.add(val1).await() == 1)
    assert(model.move(dest, val1).await())
    assert(dest.members().await() == Set(val1))
    assert(model.members().await() == Set())
  }

  test("diffStore") {
    val all = new SetKeyImpl {
      val token = this.getClass.getSimpleName + "all"
      val logger = KeyLoggerMock
      protected implicit val p = tp
    }
    val remove = new SetKeyImpl {
      val token = this.getClass.getSimpleName + "remove"
      val logger = KeyLoggerMock
      protected implicit val p = tp
    }

    assert(all.add(val1, val2).await() == 2)
    assert(remove.add(val1).await() == 1)
    assert(model.diffStore(all, remove).await() == 1)
    assert(model.members.await() == Set(val2))
  }

}
