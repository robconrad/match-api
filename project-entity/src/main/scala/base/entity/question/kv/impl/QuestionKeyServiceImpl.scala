/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 9:24 PM
 */

package base.entity.question.kv.impl

import java.util.UUID

import base.entity.kv.Key.Pipeline
import base.entity.kv.impl.HashKeyServiceImpl
import base.entity.question.kv.{ QuestionKey, QuestionKeyService }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class QuestionKeyServiceImpl
    extends HashKeyServiceImpl[UUID, QuestionKey]
    with QuestionKeyService {

  def make(id: UUID)(implicit p: Pipeline) = new QuestionKeyImpl(getKey(id), this)

}
