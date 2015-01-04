/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/3/15 2:06 PM
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

  val model: KeyImpl

  def create: Boolean

  test("exists / del") {
    assert(!model.exists().await())
    assert(!model.del().await())
    assert(create)
    assert(model.exists().await())
    assert(model.del().await())
    assert(!model.exists().await())
  }

}
