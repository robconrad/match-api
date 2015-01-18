/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:40 PM
 */

package base.entity.kv.impl

import base.entity.kv.Key._
import base.entity.kv.mock.KeyLoggerMock
import base.entity.kv.{ ListKey, KeyId, SetKey }

/**
 * {{ Describe the high level purpose of SetKeyFactoryImplTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class ListKeyServiceImplTest extends KeyServiceImplTest[ListKey] {

  private val id = KeyId("id")
  private val id2 = KeyId("id2")

  private val val1 = "value1"
  private val val2 = "value2"

  val keyService = new ListKeyServiceImpl[ListKey]() {
    // scalastyle:off null
    val serviceManifest = null
    def make(id: Id)(implicit p: Pipeline) = new ListKeyImpl {
      val token = id.toString
      val logger = KeyLoggerMock
      protected implicit val p = tp
    }
    val CHANNEL = "test"
  }
  private val model1 = keyService.make(id)
  private val model2 = keyService.make(id2)

  def create() = {
    val keys = List(model1, model2)
    keys.foreach(_.prepend(val1, val2))
    keys
  }

}
