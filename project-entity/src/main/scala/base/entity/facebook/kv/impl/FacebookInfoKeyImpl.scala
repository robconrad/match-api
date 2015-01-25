/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 1:40 PM
 */

package base.entity.facebook.kv.impl

import base.entity.facebook.FacebookInfo
import base.entity.facebook.kv.FacebookInfoKey
import base.entity.kv.Key._
import base.entity.kv.KeyLogger
import base.entity.kv.impl.SimpleKeyImpl

/**
 * {{ Describe the high level purpose of FacebookInfoKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class FacebookInfoKeyImpl(val token: Array[Byte],
                          protected val logger: KeyLogger)(implicit protected val p: Pipeline)
    extends SimpleKeyImpl[FacebookInfo] with FacebookInfoKey
