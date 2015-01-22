/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 12:25 PM
 */

package base.entity.kv.impl

import java.util.UUID

import base.common.random.RandomService
import base.entity.kv.KeyLogger
import base.entity.kv.mock.KeyLoggerMock

/**
 * {{ Describe the high level purpose of IdKeyImplTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class IdTypedKeyImplTest extends KeyImplTest {

  private val id = RandomService().uuid

  val model = new SimpleKeyImpl[UUID] with IdTypedKeyImpl {
    val token = this.getClass.getSimpleName
    val logger = KeyLoggerMock
    protected implicit val p = tp
  }

  def create = model.set(id).await()

  test("get / set") {
    assert(model.get.await() == None)
    assert(model.set(id).await())
    assert(model.get.await() == Option(id))
  }

}
