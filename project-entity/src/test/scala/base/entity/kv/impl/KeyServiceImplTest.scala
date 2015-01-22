/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 12:59 PM
 */

package base.entity.kv.impl

import base.common.service.Service
import base.entity.kv.{ KeyService, Key, KvTest }
import base.entity.service.EntityServiceTest

/**
 * {{ Describe the high level purpose of FactoryImplTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
abstract class KeyServiceImplTest[T <: Key] extends EntityServiceTest with KvTest {

  // I don't understand why but for some reason the type of service must be stable (i.e not T)
  def keyService: KeyService[_, T]
  def service: Service = keyService

  def create(): Iterable[T]

  test("del") {
    val keys = create()
    assert(keys.size > 1)
    keys.foreach(k => assert(k.exists().await()))
    assert(keyService.del(keys).await() == keys.size)
    keys.foreach(k => assert(!k.exists().await()))
  }

}
