/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/3/15 2:28 PM
 */

package base.entity.kv.impl

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
    val keys = List(factory.make("id1"), factory.make("id2"))
    keys.foreach(_.set(int).await())
    keys
  }

}
