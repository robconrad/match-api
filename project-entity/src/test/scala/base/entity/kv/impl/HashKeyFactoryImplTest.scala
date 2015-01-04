/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/3/15 2:27 PM
 */

package base.entity.kv.impl

/**
 * {{ Describe the high level purpose of HashKeyFactoryImplTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class HashKeyFactoryImplTest extends KeyFactoryImplTest {

  private val id = "id"
  private val id2 = "id2"
  private val ids = Set(id, id2)

  private val prop = "prop"
  private val prop2 = "prop2"
  private val props = Array(prop, prop2)
  private val string = "value"
  private val long = 1L

  val factory = new HashKeyFactoryImpl(KeyChannel(getClass.getSimpleName))
  private val model1 = factory.make(id)
  private val model2 = factory.make(id2)

  def create() = {
    val keys = List(model1, model2)
    keys.foreach(_.set(prop, string).await())
    keys
  }

  test("getMulti") {
    assert(factory.getMulti(prop, ids).await() == Map(id -> None, id2 -> None))
    assert(model1.set(prop, string).await())
    assert(factory.getMulti(prop, ids).await() == Map(id -> Option(string), id2 -> None))
    assert(model2.set(prop, string).await())
    assert(factory.getMulti(prop, ids).await() == Map(id -> Option(string), id2 -> Option(string)))
  }

  test("mGetMulti") {
    assert(factory.mGetMulti(props, ids).await() == Map(
      id -> Map(prop -> None, prop2 -> None),
      id2 -> Map(prop -> None, prop2 -> None)))
    assert(model1.set(prop, string).await())
    assert(model2.set(prop, string).await())
    assert(factory.mGetMulti(props, ids).await() == Map(
      id -> Map(prop -> Option(string), prop2 -> None),
      id2 -> Map(prop -> Option(string), prop2 -> None)))
    assert(model1.set(prop2, string).await())
    assert(model2.set(prop2, string).await())
    assert(factory.mGetMulti(props, ids).await() == Map(
      id -> Map(prop -> Option(string), prop2 -> Option(string)),
      id2 -> Map(prop -> Option(string), prop2 -> Option(string))))
  }

  test("incrByMulti") {
    val input = Map(id -> long, id2 -> long)
    assert(factory.getMulti(prop, ids).await() == Map(id -> None, id2 -> None))
    assert(factory.incrbyMulti(prop, input).await() == input)
    assert(factory.incrbyMulti(prop, input).await() == Map(id -> long * 2, id2 -> long * 2))
  }

}
