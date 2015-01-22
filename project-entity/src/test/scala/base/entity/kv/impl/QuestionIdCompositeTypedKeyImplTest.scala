/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 12:27 PM
 */

package base.entity.kv.impl

import base.common.random.RandomService
import base.entity.kv.mock.KeyLoggerMock
import base.entity.question.{ QuestionSides, QuestionIdComposite }

/**
 * {{ Describe the high level purpose of IdKeyImplTest here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class QuestionIdCompositeTypedKeyImplTest extends KeyImplTest {

  private val id = QuestionIdComposite(RandomService().uuid, QuestionSides.SIDE_A)

  val model = new SimpleKeyImpl[QuestionIdComposite] with QuestionIdCompositeTypedKeyImpl {
    val token = this.getClass.getSimpleName
    val logger = KeyLoggerMock
    protected implicit val p = tp
  }

  def create = model.set(id).await()

  test("get / set") {
    assert(model.get.await() == None)
    assert(model.set(id).await())
    assert(model.get.await() == Option(id))
  }

}
