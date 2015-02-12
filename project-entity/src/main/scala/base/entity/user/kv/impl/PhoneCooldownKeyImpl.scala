/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 8:49 PM
 */

package base.entity.user.kv.impl

import base.entity.kv.impl.SimpleKeyImpl
import base.entity.user.kv.PhoneCooldownKey

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class PhoneCooldownKeyImpl(val keyValue: String)
    extends SimpleKeyImpl[String, Int]
    with PhoneCooldownKey
