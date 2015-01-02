/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/1/15 8:46 PM
 */

package base.entity.kv.impl

import base.entity.kv.model.{ IntModel, IntObject }

/**
 * {{ Describe the high level purpose of IntModelTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class IntModelTest extends ModelTest {

  val id = "id"

  val int = 10

  val obj = new IntObject {
    val CHANNEL = "channel"
  }

  val model = new IntModel(obj, id) {}

  test("incr") {
    assert(model.get().await() == None)
    assert(model.incr().await() == 1)
    assert(model.incr().await() == 2)
  }

  test("get / set") {
    assert(model.get().await() == None)
    assert(model.set(int).await())
    assert(model.get().await() == Option(int))
  }

}
