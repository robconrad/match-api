/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 9:38 PM
 */

package base.entity.kv.impl

import base.entity.kv.KvTest

/**
 * {{ Describe the high level purpose of KeyImplTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
abstract class KeyImplTest extends KvTest {

  val model: ScredisKeyImpl[_]

  def create: Boolean

  test("exists / del") {
    assert(!model.exists.await())
    assert(!model.del().await())
    assert(create)
    assert(model.exists.await())
    assert(model.del().await())
    assert(!model.exists.await())
  }

  test("expire / ttl") {
    assert(!model.expire(Int.MaxValue).await())
    assert(model.ttl.await() == Left(false))
    assert(create)
    assert(model.expire(Int.MaxValue).await())
    model.ttl.await() match {
      case Left(b) => fail()
      case Right(ttl) => assert(ttl > 0)
    }
  }

}
