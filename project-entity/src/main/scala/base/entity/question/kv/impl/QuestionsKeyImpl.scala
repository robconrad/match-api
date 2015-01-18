/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 10:27 PM
 */

package base.entity.question.kv.impl

import base.entity.kv.KeyLogger
import base.entity.kv.impl.SetKeyImpl
import base.entity.question.kv.QuestionsKey

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class QuestionsKeyImpl(val token: String, protected val logger: KeyLogger)
    extends SetKeyImpl with QuestionsKey {

}
