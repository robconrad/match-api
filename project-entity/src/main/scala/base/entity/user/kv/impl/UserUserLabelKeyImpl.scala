/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/17/15 7:27 PM
 */

package base.entity.user.kv.impl

import base.entity.kv.KeyLogger
import base.entity.kv.impl.StringKeyImpl
import base.entity.user.kv.UserUserLabelKey

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class UserUserLabelKeyImpl(val token: String, protected val logger: KeyLogger)
    extends StringKeyImpl with UserUserLabelKey {

}
