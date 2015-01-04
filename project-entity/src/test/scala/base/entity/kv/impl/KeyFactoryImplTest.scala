/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/3/15 4:19 PM
 */

package base.entity.kv.impl

import base.entity.kv.{ Key, KvTest }

/**
 * {{ Describe the high level purpose of FactoryImplTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
abstract class KeyFactoryImplTest[T <: Key] extends KvTest {

  def factory: KeyFactoryImpl

  def create(): Iterable[Key]

  test("del") {
    val keys = create()
    assert(keys.size > 1)
    keys.foreach(k => assert(k.exists().await()))
    assert(factory.del(keys).await() == keys.size)
    keys.foreach(k => assert(!k.exists().await()))
  }

}
