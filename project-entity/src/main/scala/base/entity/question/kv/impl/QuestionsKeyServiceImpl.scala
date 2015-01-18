/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:34 PM
 */

package base.entity.question.kv.impl

import base.entity.kv.Key.{ Pipeline, Id }
import base.entity.kv.impl.SetKeyServiceImpl
import base.entity.question.kv.{ QuestionsKeyService, QuestionsKey }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class QuestionsKeyServiceImpl
    extends SetKeyServiceImpl[QuestionsKey] with QuestionsKeyService {

  def make(id: Id)(implicit p: Pipeline) = new QuestionsKeyImpl(getKey(id), this)

}
