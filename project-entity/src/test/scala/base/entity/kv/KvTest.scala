/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/3/15 1:13 PM
 */

package base.entity.kv

import base.entity.test.EntityBaseSuite
import org.scalatest.BeforeAndAfterEach

/**
 * {{ Describe the high level purpose of ModelTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
abstract class KvTest extends EntityBaseSuite with BeforeAndAfterEach {

  implicit val p = KvService().client.pipeline()

  override def beforeEach() {
    super.beforeEach()
    KvService().client.flushall()
  }

}
