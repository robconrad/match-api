/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/7/15 3:34 PM
 */

package base.entity.kv.impl

import java.util.UUID

import base.common.random.RandomService
import base.entity.kv.{ KvTest, KeyPrefixes }

/**
 * {{ Describe the high level purpose of IntModelTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class SetKeyImplTest extends /* KeyImplTest */ KvTest {

  private val val1 = RandomService().uuid
  private val val2 = RandomService().uuid

  val model = new SetKeyImpl[String, UUID] {
    def keyPrefix = KeyPrefixes.test
    def keyValue = "test"
  }

  def create = model.add(val1).await() == 1

  test("members") {
    assert(model.members.await() == Set())
    assert(model.add(val1).await() == 1)
    assert(model.members.await() == Set(val1))
    assert(model.add(val2).await() == 1)
    assert(model.members.await() == Set(val1, val2))
  }

  test("isMember") {
    assert(!model.isMember(val1).await())
    assert(model.add(val1).await() == 1)
    assert(model.isMember(val1).await())
  }

  test("rand") {
    assert(model.randMembers().await() == Set())
    assert(model.add(val1).await() == 1)
    assert(model.randMembers().await() == Set(val1))
  }

  test("rand(count)") {
    val count = 3
    assert(model.randMembers(count).await() == Set())
    assert(model.add(val1, val2).await() == 2)
    assert(model.randMembers(count).await() == Set(val1, val2))
  }

  test("pop") {
    assert(model.pop().await() == None)
    assert(model.add(val1).await() == 1)
    assert(model.pop().await() == Option(val1))
    assert(model.pop().await() == None)
  }

  test("add") {
    assert(model.add(val1).await() == 1)
    assert(model.add(val1).await() == 0)
    assert(model.add(val1, val2).await() == 1)
  }

  test("remove") {
    assert(model.rem(val1).await() == 0)
    assert(model.add(val1).await() == 1)
    assert(model.rem(val1).await() == 1)
  }

  test("move") {
    val dest = new SetKeyImpl[String, UUID] {
      def keyPrefix = KeyPrefixes.test
      def keyValue = "dest"
    }
    assert(model.add(val1).await() == 1)
    assert(model.move(dest, val1).await())
    assert(dest.members.await() == Set(val1))
    assert(model.members.await() == Set())
  }

  test("diffStore") {
    val all = new SetKeyImpl[String, UUID] {
      def keyPrefix = KeyPrefixes.test
      def keyValue = "all"
    }
    val remove = new SetKeyImpl[String, UUID] {
      def keyPrefix = KeyPrefixes.test
      def keyValue = "remove"
    }

    assert(all.add(val1, val2).await() == 2)
    assert(remove.add(val1).await() == 1)
    assert(model.diffStore(all, remove).await() == 1)
    assert(model.members.await() == Set(val2))
  }

  /*

  test("remove multi") {
    assert(keyService.rem(List(), val1).await() == Map())

    assert(keyService.rem(models, val1).await() == Map(model1 -> false, model2 -> false))

    assert(model1.add(val1).await() == 1)
    assert(keyService.rem(models, val1).await() == Map(model1 -> true, model2 -> false))
    assert(!model1.isMember(val1).await())

    assert(model1.add(val1).await() == 1)
    assert(model2.add(val1, val2).await() == 2)
    assert(keyService.rem(models, val1).await() == Map(model1 -> true, model2 -> true))
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

   */

}
