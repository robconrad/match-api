/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 4:56 PM
 */

package base.entity.question.kv.impl

import base.entity.kv.Key.Pipeline
import base.entity.kv.impl.SetKeyServiceImpl
import base.entity.question.kv.{ QuestionsKey, QuestionsKeyService }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class QuestionsKeyServiceImpl
    extends SetKeyServiceImpl[String, QuestionsKey]
    with QuestionsKeyService {

  def make(id: String)(implicit p: Pipeline) = new QuestionsKeyImpl(getKey(id), this)

}
