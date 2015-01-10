/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/10/15 3:51 PM
 */

package base.entity.kv.impl

import base.entity.kv.{ KeyId, KeyChannel }

/**
 * {{ Describe the high level purpose of IntKeyFactoryImplTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class IntKeyFactoryImplTest extends KeyFactoryImplTest {

  private val int = 10

  val factory = new IntKeyFactoryImpl(KeyChannel(getClass.getSimpleName))

  def create() = {
    val keys = List(factory.make(KeyId("id1")), factory.make(KeyId("id2")))
    keys.foreach(_.set(int).await())
    keys
  }

}
