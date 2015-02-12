/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 8:55 PM
 */

package base.entity.kv.impl

import java.util.UUID

import base.common.random.RandomService
import base.entity.kv.{KvTest, KeyPrefixes}

/**
 * {{ Describe the high level purpose of IdKeyImplTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class IdTypedKeyImplTest extends /* KeyImplTest */ KvTest {

  private val id = RandomService().uuid

  val model = new SimpleKeyImpl[String, UUID] {
    def keyPrefix = KeyPrefixes.test
    def keyValue = "test"
  }

  def create = model.set(id).await()

  test("get / set") {
    assert(model.get.await() == None)
    assert(model.set(id).await())
    assert(model.get.await() == Option(id))
  }

}
