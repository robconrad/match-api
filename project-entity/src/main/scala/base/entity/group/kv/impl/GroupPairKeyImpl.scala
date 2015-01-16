/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 4:46 PM
 */

package base.entity.group.kv.impl

import base.entity.group.kv.GroupPairKey
import base.entity.kv.KeyLogger
import base.entity.kv.impl.IdKeyImpl

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupPairKeyImpl(val token: String, protected val logger: KeyLogger)
    extends IdKeyImpl with GroupPairKey {

}
