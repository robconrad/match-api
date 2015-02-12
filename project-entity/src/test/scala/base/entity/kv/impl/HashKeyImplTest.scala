/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 8:03 PM
 */

package base.entity.kv.impl

import base.entity.kv.Key.Prop
import base.entity.kv.{KvTest, KeyPrefixes, KeyProp}

import scala.language.reflectiveCalls

/**
 * {{ Describe the high level purpose of HashModelTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
// scalastyle:off null
class HashKeyImplTest extends /* KeyImplTest */ KvTest {

  def create = key.create

  val key = new HashKeyImpl[String] {

    private val prop = new KeyProp("prop") {}
    private val prop2 = new KeyProp("prop2") {}
    private val string = "value"
    private val string2 = "value2"
    private val int = 1
    private val long = 1L
    private val flagOn = true
    private val flagOff = false
    private val map = Map[Prop, Array[Byte]](prop -> write(string), prop2 -> write(string))

    def keyPrefix = KeyPrefixes.test

    def keyValue = "test"

    def create = set(prop, write(string)).await()

    test("getString") {
      assert(get(prop).await() == None)
      assert(set(prop, write(string)).await())
      assert(get(prop).await().map(read[String]) == Option(string))
    }

    test("getInt") {
      assert(get(prop).await() == None)
      assert(set(prop, write(int)).await())
      assert(get(prop).await().map(read[Int]) == Option(int))
    }

    test("getLong") {
      assert(get(prop).await() == None)
      assert(set(prop, write(long)).await())
      assert(get(prop).await().map(read[Long]) == Option(long))
    }

    test("getFlag") {
      assert(get(prop).await() == None)
      assert(set(prop, write(flagOn)).await())
      assert(get(prop).await().map(read[Boolean]).contains(flagOn))
      assert(!set(prop, write(flagOff)).await())
      assert(get(prop).await().map(read[Boolean]).contains(flagOff))
    }

    test("getAll") {
      assert(getAll.await() == None)
      mSet(map).await()
      assert(getAll.await().get.map { case (k,v) => key -> read[String](v) } ==
        map.map { case (k, v) => key -> read[String](v)})
    }

    test("incrby") {
      assert(get(prop).await() == None)
      assert(incrBy(prop, 0L).await() == 0L)
      assert(incrBy(prop, long).await() == long)
      assert(incrBy(prop, long).await() == long + long)
    }

    test("mGet") {
      assert(mGet(map.keys.toSeq: _*).await() == List(None, None))
      mSet(map).await()
      assert(mGet(map.keys.toSeq: _*).await().map(_.map(read[String])) == List(Option(string), Option(string)))
    }

    test("getKeys") {
      assert(keys.await() == Set())
      mSet(map).await()
      assert(keys.await() == map.keys.toSet)
    }

    test("setNx") {
      assert(get(prop).await() == None)
      assert(setNX(prop, write(string)).await())
      assert(get(prop).await().map(read[String]) == Option(string))
      assert(!setNX(prop, write(string2)).await())
      assert(get(prop).await().map(read[String]) == Option(string))
    }

    test("del") {
      assert(set(prop, write(string)).await())
      assert(del(prop).await() == 1L)
      assert(get(prop).await() == None)
    }

    test("del-multi") {
      mSet(map).await()
      assert(del(prop, prop2).await() == 2L)
      assert(get(prop).await() == None)
      assert(get(prop2).await() == None)
    }

  }

}
