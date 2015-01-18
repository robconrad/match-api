/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:40 PM
 */

package base.entity.kv.impl

import base.entity.kv.Key._
import base.entity.kv.mock.KeyLoggerMock
import base.entity.kv.{ IntKey, IntKeyService, KeyId }

/**
 * {{ Describe the high level purpose of IntKeyFactoryImplTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class IntKeyServiceImplTest extends KeyServiceImplTest[IntKey] {

  private val int = 10

  val keyService = new IntKeyServiceImpl[IntKey]() {
    // scalastyle:off null
    val serviceManifest = null
    def make(id: Id)(implicit p: Pipeline) = new IntKeyImpl {
      val token = id.toString
      val logger = KeyLoggerMock
      protected implicit val p = tp
    }
    val CHANNEL = "test"
  }

  def create() = {
    val keys = List(keyService.make(KeyId("id1")), keyService.make(KeyId("id2")))
    keys.foreach(_.set(int).await())
    keys
  }

}
