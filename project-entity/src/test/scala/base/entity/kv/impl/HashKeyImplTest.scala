/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/11/15 3:53 PM
 */

package base.entity.kv.impl

import base.entity.kv.Key.Prop
import base.entity.kv.KeyProp
import base.entity.kv.mock.KeyLoggerMock

import scala.language.reflectiveCalls

/**
 * {{ Describe the high level purpose of HashModelTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
// scalastyle:off null
class HashKeyImplTest extends KeyImplTest {

  private val prop = new KeyProp("prop") {}
  private val prop2 = new KeyProp("prop2") {}
  private val string = "value"
  private val string2 = "value2"
  private val int = 1
  private val long = 1L
  private val flagOn = true
  private val flagOff = false
  private val map = Map[Prop, String](prop -> string, prop2 -> string)

  val model = new HashKeyImpl(getClass.getSimpleName, KeyLoggerMock)

  def create = model.set(prop, string).await()

  test("getString") {
    assert(model.getString(prop).await() == None)
    assert(model.set(prop, string).await())
    assert(model.getString(prop).await() == Option(string))
  }

  test("getInt") {
    assert(model.getInt(prop).await() == None)
    assert(model.set(prop, int).await())
    assert(model.getInt(prop).await() == Option(int))
  }

  test("getLong") {
    assert(model.getLong(prop).await() == None)
    assert(model.set(prop, long).await())
    assert(model.getLong(prop).await() == Option(long))
  }

  test("getFlag") {
    assert(model.getFlag(prop).await() == flagOff)
    assert(model.setFlag(prop, flagOn).await())
    assert(model.getFlag(prop).await() == flagOn)
    assert(model.setFlag(prop, flagOff).await())
    assert(model.getFlag(prop).await() == flagOff)
  }

  test("getAll") {
    assert(model.get.await() == Map())
    assert(model.set(map).await())
    assert(model.get.await() == map)
  }

  test("incrby") {
    assert(model.getInt(prop).await() == None)
    assert(model.increment(prop, 0L).await() == 0L)
    assert(model.increment(prop, long).await() == long)
    assert(model.increment(prop, long).await() == long + long)
  }

  test("mGet") {
    assert(model.get(map.keys.toArray).await() == Map(prop -> None, prop2 -> None))
    assert(model.set(map).await())
    assert(model.get(map.keys.toArray).await() == Map(prop -> Option(string), prop2 -> Option(string)))
  }

  test("getKeys") {
    assert(model.getProps.await() == List())
    assert(model.set(map).await())
    assert(model.getProps.await().map(_.toString).sorted == map.keys.toList.map(_.toString).sorted)
  }

  test("setNx") {
    assert(model.getString(prop).await() == None)
    assert(model.setNx(prop, string).await())
    assert(model.getString(prop).await() == Option(string))
    assert(!model.setNx(prop, string2).await())
    assert(model.getString(prop).await() == Option(string))
  }

  test("del") {
    assert(model.set(prop, string).await())
    assert(model.del(prop).await())
    assert(model.getString(prop).await() == None)
  }

  test("del-multi") {
    assert(model.set(map).await())
    assert(model.del(List(prop, prop2)).await())
    assert(model.getString(prop).await() == None)
    assert(model.getString(prop2).await() == None)
  }

}
