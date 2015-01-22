/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 11:54 AM
 */

package base.entity.question.kv.impl

import base.entity.kv.Key.Pipeline
import base.entity.kv.KeyLogger
import base.entity.kv.impl.{ QuestionIdCompositeTypedKeyImpl, SetKeyImpl }
import base.entity.question.QuestionIdComposite
import base.entity.question.kv.QuestionsKey

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class QuestionsKeyImpl(val token: String, protected val logger: KeyLogger)(implicit protected val p: Pipeline)
    extends SetKeyImpl[QuestionIdComposite] with QuestionsKey with QuestionIdCompositeTypedKeyImpl {

}
