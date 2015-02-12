/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 8:49 PM
 */

package base.entity.facebook.kv.impl

import base.entity.facebook.FacebookInfo
import base.entity.facebook.kv.FacebookInfoKey
import base.entity.kv.impl.SimpleKeyImpl

/**
 * {{ Describe the high level purpose of FacebookInfoKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class FacebookInfoKeyImpl(val keyValue: String)
    extends SimpleKeyImpl[String, FacebookInfo]
    with FacebookInfoKey
