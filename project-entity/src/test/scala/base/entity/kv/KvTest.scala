/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/12/15 8:49 PM
 */

package base.entity.kv

import base.entity.test.EntityBaseSuite
import org.scalatest.{ BeforeAndAfterAll, BeforeAndAfterEach }

/**
 * {{ Describe the high level purpose of ModelTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait KvTest extends EntityBaseSuite with BeforeAndAfterAll with BeforeAndAfterEach with MakeKey {

  override def beforeEach() {
    super.beforeEach()
    KeyCommandsService().client.flushAll().await()
  }

}
