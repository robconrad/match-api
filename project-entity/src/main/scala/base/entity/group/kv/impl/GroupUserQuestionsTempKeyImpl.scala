/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:30 PM
 */

package base.entity.group.kv.impl

import base.entity.group.kv.GroupUserQuestionsTempKey
import base.entity.kv.Key.Pipeline
import base.entity.kv.KeyLogger
import base.entity.kv.impl.SetKeyImpl

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupUserQuestionsTempKeyImpl(val token: String,
                                    protected val logger: KeyLogger)(implicit protected val p: Pipeline)
    extends SetKeyImpl with GroupUserQuestionsTempKey {

}
