/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/13/15 10:20 PM
 */

package base.entity.kv.impl

import base.entity.kv.Key.{ Id, Prop }
import base.entity.kv.mock.{ PrivateHashKeyMock, KeyLoggerMock }
import base.entity.kv._

/**
 * {{ Describe the high level purpose of HashKeyFactoryImplTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class PrivateHashKeyServiceImplTest extends KeyServiceImplTest[PrivateHashKey] {

  private val id = KeyId("id")
  private val id2 = KeyId("id2")
  private val ids = Set(id, id2)

  private val prop = new KeyProp("prop") {}
  private val prop2 = new KeyProp("prop2") {}
  private val props = Array[Prop](prop, prop2)
  private val string = "value"
  private val long = 1L

  val keyService = new HashKeyServiceImpl[PrivateHashKey]() {
    // scalastyle:off null
    val serviceManifest = null
    def make(id: Id) = new PrivateHashKeyImpl(s"$CHANNEL-$id", KeyLoggerMock)
    val CHANNEL = "test"
  }
  private val model1 = keyService.make(id)
  private val model2 = keyService.make(id2)

  def create() = {
    val keys = List(model1, model2)
    keys.foreach(_.set(prop, string).await())
    keys
  }

  test("getMulti") {
    assert(keyService.getMulti(prop, List()).await() == Map())
    assert(keyService.getMulti(prop, ids).await() == Map(id -> None, id2 -> None))
    assert(model1.set(prop, string).await())
    assert(keyService.getMulti(prop, ids).await() == Map(id -> Option(string), id2 -> None))
    assert(model2.set(prop, string).await())
    assert(keyService.getMulti(prop, ids).await() == Map(id -> Option(string), id2 -> Option(string)))
  }

  test("mGetMulti") {
    assert(keyService.mGetMulti(props, List()).await() == Map())
    assert(keyService.mGetMulti(props, ids).await() == Map(
      id -> Map(prop -> None, prop2 -> None),
      id2 -> Map(prop -> None, prop2 -> None)))
    assert(model1.set(prop, string).await())
    assert(model2.set(prop, string).await())
    assert(keyService.mGetMulti(props, ids).await() == Map(
      id -> Map(prop -> Option(string), prop2 -> None),
      id2 -> Map(prop -> Option(string), prop2 -> None)))
    assert(model1.set(prop2, string).await())
    assert(model2.set(prop2, string).await())
    assert(keyService.mGetMulti(props, ids).await() == Map(
      id -> Map(prop -> Option(string), prop2 -> Option(string)),
      id2 -> Map(prop -> Option(string), prop2 -> Option(string))))
  }

  test("incrByMulti") {
    val input = Map[Id, Long](id -> long, id2 -> long)
    assert(keyService.incrbyMulti(prop, Map()).await() == Map())
    assert(keyService.getMulti(prop, ids).await() == Map(id -> None, id2 -> None))
    assert(keyService.incrbyMulti(prop, input).await() == input)
    assert(keyService.incrbyMulti(prop, input).await() == Map(id -> long * 2, id2 -> long * 2))
  }

}