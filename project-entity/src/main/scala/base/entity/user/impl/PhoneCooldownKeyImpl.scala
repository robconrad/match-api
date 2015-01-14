/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/13/15 6:26 PM
 */

package base.entity.user.impl

import base.entity.kv.KeyLogger
import base.entity.kv.impl.IntKeyImpl
import base.entity.user.PhoneCooldownKey

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class PhoneCooldownKeyImpl(val token: String, protected val logger: KeyLogger)
    extends IntKeyImpl with PhoneCooldownKey {

}
