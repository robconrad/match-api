/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 10:57 AM
 */

package base.entity.user.kv.impl

import base.entity.kv.Key.Pipeline
import base.entity.kv.KeyLogger
import base.entity.kv.impl.SimpleKeyImpl
import base.entity.user.kv.UserPhoneLabelKey

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class UserPhoneLabelKeyImpl(val token: Array[Byte], protected val logger: KeyLogger)(implicit protected val p: Pipeline)
    extends SimpleKeyImpl[String] with UserPhoneLabelKey {

}
