/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/1/15 9:19 PM
 */

package base.entity.kv.impl

import base.entity.kv.model.{ SetModel, SetObject }

/**
 * {{ Describe the high level purpose of IntModelTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class SetModelTest extends ModelTest {

  val id = "id"

  val val1 = "value1"
  val val2 = "value2"
  val val3 = "value3"

  val obj = new SetObject {
    val CHANNEL = "channel"
  }

  val model = new SetModel(obj, id) {}

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
    val dest = new SetModel(obj, "destination") {}
    assert(model.add(val1).await())
    assert(model.move(dest, val1).await())
    assert(dest.members().await() == Set(val1))
    assert(model.members().await() == Set())
  }

  test("remove multi") {

  }

  test("count") {

  }

  test("unionStore") {

  }

}
