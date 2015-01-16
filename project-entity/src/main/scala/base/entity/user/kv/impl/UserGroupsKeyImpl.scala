/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 4:49 PM
 */

package base.entity.user.kv.impl

import base.entity.kv.KeyLogger
import base.entity.kv.impl.SetKeyImpl
import base.entity.user.kv.UserGroupsKey

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class UserGroupsKeyImpl(val token: String, protected val logger: KeyLogger)
    extends SetKeyImpl with UserGroupsKey {

}
