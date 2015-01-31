/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/26/15 9:30 PM
 */

package base.entity.user.kv.impl

import java.util.UUID

import base.entity.kv.Key.Pipeline
import base.entity.kv.KeyLogger
import base.entity.kv.impl.SimpleKeyImpl
import base.entity.user.kv.FacebookUserKey

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class FacebookUserKeyImpl(val token: Array[Byte], protected val logger: KeyLogger)(implicit protected val p: Pipeline)
    extends SimpleKeyImpl[UUID] with FacebookUserKey {

}
