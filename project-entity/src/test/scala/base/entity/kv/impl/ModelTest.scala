/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/1/15 8:24 PM
 */

package base.entity.kv.impl

import base.entity.kv.KvService
import base.entity.test.EntityBaseSuite
import org.scalatest.BeforeAndAfterEach

/**
 * {{ Describe the high level purpose of ModelTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
abstract class ModelTest extends EntityBaseSuite with BeforeAndAfterEach {

  override def beforeEach() {
    super.beforeEach()
    KvService().client.flushall()
  }

}
