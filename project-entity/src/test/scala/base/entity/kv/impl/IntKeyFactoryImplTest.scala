/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 1:37 PM
 */

package base.entity.kv.impl

import base.entity.kv.{ IntKeyFactory, KeyFactoryLocator, KeyId }

/**
 * {{ Describe the high level purpose of IntKeyFactoryImplTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class IntKeyFactoryImplTest extends KeyFactoryImplTest {

  private val int = 10

  val factory = new IntKeyFactoryImpl(new KeyFactoryLocator[IntKeyFactory](getClass.getSimpleName) {})

  def create() = {
    val keys = List(factory.make(KeyId("id1")), factory.make(KeyId("id2")))
    keys.foreach(_.set(int).await())
    keys
  }

}
