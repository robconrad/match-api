/*
 * Copyright (c) 2014 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 12/25/14 10:32 AM
 */

package base.entity.db.impl

import base.entity.db.EvolutionService
import base.entity.service.EntityServiceTest

/**
 * Responsible for testing the logic and side effects of the Impl
 *  (i.e. validation, persistence, etc.)
 * @author rconrad
 */
class EvolutionServiceImplTest extends EntityServiceTest {

  val service = EvolutionService()

  override def beforeAll() {
    super.beforeAll()
    destroyDb()
  }

  /**
   * Intentionally fail if an evolution is added and this test is not updated.
   *  It is here to remind you of the consequences of adding an evolution when we
   *  deploy to production, as well as the consequences of having a large number
   *  overall. Be very careful adding these and think through all implications.
   */
  test("current evolution level") {
    assert(service.getEvolutionLevel.await() == 0, "evolution level 0 (empty db)")
    service.evolve().awaitLong()
    assert(service.getEvolutionLevel.await() == 1, "evolution level 1 (populated)")
  }

}
