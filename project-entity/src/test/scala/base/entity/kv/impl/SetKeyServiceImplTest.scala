/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 12:01 PM
 */

package base.entity.kv.impl

import base.entity.kv.Key.{ Pipeline, Id }
import base.entity.kv.{ SetKey, KeyId }
import base.entity.kv.mock.KeyLoggerMock

/**
 * {{ Describe the high level purpose of SetKeyFactoryImplTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class SetKeyServiceImplTest extends KeyServiceImplTest[SetKey[String]] {

  private val id = KeyId("id")
  private val id2 = KeyId("id2")
  private val id3 = KeyId("id3")

  private val val1 = "value1"
  private val val2 = "value2"

  val keyService = new SetKeyServiceImpl[String, SetKey[String]]() {
    // scalastyle:off null
    val serviceManifest = null
    def make(id: Id)(implicit p: Pipeline) = new SetKeyImpl[String] with StringTypedKeyImpl {
      val token = id.toString
      val logger = KeyLoggerMock
      protected implicit val p = tp
    }
    val CHANNEL = "test"
  }
  private val model1 = keyService.make(id)
  private val model2 = keyService.make(id2)
  private val dest = keyService.make(id3)
  private val models = List(model1, model2)

  def create() = {
    val keys = List(model1, model2)
    keys.foreach(_.add(val1))
    keys
  }

  test("remove multi") {
    assert(keyService.remove(List(), val1).await() == Map())

    assert(keyService.remove(models, val1).await() == Map(model1 -> false, model2 -> false))

    assert(model1.add(val1).await() == 1)
    assert(keyService.remove(models, val1).await() == Map(model1 -> true, model2 -> false))
    assert(!model1.isMember(val1).await())

    assert(model1.add(val1).await() == 1)
    assert(model2.add(val1, val2).await() == 2)
    assert(keyService.remove(models, val1).await() == Map(model1 -> true, model2 -> true))
    assert(!model1.isMember(val1).await())
    assert(!model2.isMember(val1).await())
    assert(model2.isMember(val2).await())
  }

  test("count") {
    assert(keyService.count(List()).await() == Map())

    assert(keyService.count(models).await() == Map(model1 -> 0, model2 -> 0))

    assert(model1.add(val1).await() == 1)
    assert(model2.add(val1, val2).await() == 2)
    assert(keyService.count(models).await() == Map(model1 -> 1, model2 -> 2))
  }

  test("unionStore") {
    assert(keyService.unionStore(dest).await() == 0)

    assert(model1.add(val1, val2).await() == 2)
    assert(model2.add(val2).await() == 1)
    assert(keyService.unionStore(dest, model1, model2).await() == 2)
    assert(dest.isMember(val1).await())
    assert(dest.isMember(val2).await())
  }

}
