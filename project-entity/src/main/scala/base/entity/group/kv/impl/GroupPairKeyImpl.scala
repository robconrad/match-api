/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 3:05 PM
 */

package base.entity.group.kv.impl

import java.util.UUID

import base.entity.group.kv.GroupPairKey
import base.entity.kv.Key.Pipeline
import base.entity.kv.KeyLogger
import base.entity.kv.impl.{ IdTypedKeyImpl, SimpleKeyImpl }

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupPairKeyImpl(val token: Array[Byte], protected val logger: KeyLogger)(implicit protected val p: Pipeline)
    extends SimpleKeyImpl[UUID] with GroupPairKey with IdTypedKeyImpl {

}
