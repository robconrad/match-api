/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 4:48 PM
 */

package base.entity.group.kv.impl

import base.entity.group.kv.GroupPhonesInvitedKey
import base.entity.kv.Key.Pipeline
import base.entity.kv.KeyLogger
import base.entity.kv.impl.SetKeyImpl

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupPhonesInvitedKeyImpl(val token: Array[Byte],
                                protected val logger: KeyLogger)(implicit protected val p: Pipeline)
    extends SetKeyImpl[String] with GroupPhonesInvitedKey {

}
