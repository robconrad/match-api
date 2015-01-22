/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 11:16 AM
 */

package base.entity.group.kv.impl

import java.util.UUID

import base.entity.group.kv.GroupUsersKey
import base.entity.kv.Key.Pipeline
import base.entity.kv.KeyLogger
import base.entity.kv.impl.{ IdTypedKeyImpl, SetKeyImpl }

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupUsersKeyImpl(val token: String, protected val logger: KeyLogger)(implicit protected val p: Pipeline)
    extends SetKeyImpl[UUID] with IdTypedKeyImpl with GroupUsersKey {

}
