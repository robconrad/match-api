/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/1/15 8:24 PM
 */

package base.entity.kv.impl

import base.entity.kv.KvService
import base.entity.kv.model.{ HashModel, HashObject }
import base.entity.test.EntityBaseSuite
import org.scalatest.BeforeAndAfterEach

import scala.language.reflectiveCalls

/**
 * {{ Describe the high level purpose of HashModelTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
// scalastyle:off null
class HashModelTest extends ModelTest {

  val id = "id"
  val id2 = "id2"
  val ids = Set(id, id2)

  val prop = "prop"
  val prop2 = "prop2"
  val props = Array(prop, prop2)
  val string = "value"
  val string2 = "value2"
  val int = 1
  val long = 1L
  val flagOn = true
  val flagOff = false
  val map = Map(prop -> string, prop2 -> string)

  val obj = new HashObject {
    val CHANNEL = "HashModelTest"

    class HashModelInstance(id: String) extends HashModel(this, id) {
      def setProp(prop: String) {
        assert(set(prop, string).await())
      }
    }

    val model1 = new HashModelInstance(id)
    val model2 = new HashModelInstance(id2)

    def testGetMulti() {
      assert(getMulti(prop, ids).await() == Map(id -> None, id2 -> None))
      model1.setProp(prop)
      assert(getMulti(prop, ids).await() == Map(id -> Option(string), id2 -> None))
      model2.setProp(prop)
      assert(getMulti(prop, ids).await() == Map(id -> Option(string), id2 -> Option(string)))
    }

    def testMGetMulti() {
      assert(mGetMulti(props, ids).await() == Map(
        id -> Map(prop -> None, prop2 -> None),
        id2 -> Map(prop -> None, prop2 -> None)))
      model1.setProp(prop)
      model2.setProp(prop)
      assert(mGetMulti(props, ids).await() == Map(
        id -> Map(prop -> Option(string), prop2 -> None),
        id2 -> Map(prop -> Option(string), prop2 -> None)))
      model1.setProp(prop2)
      model2.setProp(prop2)
      assert(mGetMulti(props, ids).await() == Map(
        id -> Map(prop -> Option(string), prop2 -> Option(string)),
        id2 -> Map(prop -> Option(string), prop2 -> Option(string))))
    }

    def testIncrByMulti() {
      val input = Map(id -> long, id2 -> long)
      assert(getMulti(prop, ids).await() == Map(id -> None, id2 -> None))
      assert(incrbyMulti(prop, input).await() == input)
      assert(incrbyMulti(prop, input).await() == Map(id -> long * 2, id2 -> long * 2))
    }

  }

  val model = new HashModel(obj, id) {

    def testGetString() {
      assert(getString(prop).await() == None)
      assert(set(prop, string).await())
      assert(getString(prop).await() == Option(string))
    }

    def testGetInt() {
      assert(getInt(prop).await() == None)
      assert(set(prop, int).await())
      assert(getInt(prop).await() == Option(int))
    }

    def testGetLong() {
      assert(getLong(prop).await() == None)
      assert(set(prop, long).await())
      assert(getLong(prop).await() == Option(long))
    }

    def testGetFlag() {
      assert(getFlag(prop).await() == flagOff)
      assert(setFlag(prop, flagOn).await())
      assert(getFlag(prop).await() == flagOn)
      assert(setFlag(prop, flagOff).await())
      assert(getFlag(prop).await() == flagOff)
    }

    def testGet() {
      assert(get(prop).await() == null)
      assert(set(prop, string).await())
      assert(get(prop).await() == string)
    }

    def testGetAll() {
      assert(getAll.await() == Map())
      assert(set(map).await())
      assert(getAll.await() == map)
    }

    def testIncrBy() {
      assert(getInt(prop).await() == None)
      assert(incrby(prop, 0L).await() == 0L)
      assert(incrby(prop, long).await() == long)
      assert(incrby(prop, long).await() == long + long)
    }

    def testMGet() {
      assert(mGet(map.keys.toArray).await() == Map(prop -> None, prop2 -> None))
      assert(set(map).await())
      assert(mGet(map.keys.toArray).await() == Map(prop -> Option(string), prop2 -> Option(string)))
    }

    def testGetKeys() {
      assert(getKeys.await() == List())
      assert(set(map).await())
      assert(getKeys.await().sorted == map.keys.toList.sorted)
    }

    def testSetNx() {
      assert(getString(prop).await() == None)
      assert(setNx(prop, string).await())
      assert(getString(prop).await() == Option(string))
      assert(!setNx(prop, string2).await())
      assert(getString(prop).await() == Option(string))
    }

    def testDel() {
      assert(set(prop, string).await())
      assert(del(prop).await())
      assert(getString(prop).await() == None)
    }

  }

  test("getString") {
    model.testGetString()
  }

  test("getInt") {
    model.testGetInt()
  }

  test("getLong") {
    model.testGetLong()
  }

  test("getFlag") {
    model.testGetFlag()
  }

  test("get") {
    model.testGet()
  }

  test("getAll") {
    model.testGetAll()
  }

  test("incrby") {
    model.testIncrBy()
  }

  test("mGet") {
    model.testMGet()
  }

  test("getKeys") {
    model.testGetKeys()
  }

  test("setNx") {
    model.testSetNx()
  }

  test("del") {
    model.testDel()
  }

  test("getMulti") {
    obj.testGetMulti()
  }

  test("mGetMulti") {
    obj.testMGetMulti()
  }

  test("incrByMulti") {
    obj.testIncrByMulti()
  }

}
