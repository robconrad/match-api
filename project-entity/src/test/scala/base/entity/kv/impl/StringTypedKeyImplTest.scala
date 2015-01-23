/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 5:00 PM
 */

package base.entity.kv.impl

import base.common.random.RandomService
import base.entity.kv.mock.KeyLoggerMock

/**
 * {{ Describe the high level purpose of IdKeyImplTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class StringTypedKeyImplTest extends KeyImplTest {

  private val string = RandomService().uuid.toString

  val model = new SimpleKeyImpl[String] {
    val token = this.getClass.getSimpleName.getBytes
    val logger = KeyLoggerMock
    protected implicit val p = tp
  }

  def create = model.set(string).await()

  test("get / set") {
    assert(model.get.await() == None)
    assert(model.set(string).await())
    assert(model.get.await() == Option(string))
  }

}
