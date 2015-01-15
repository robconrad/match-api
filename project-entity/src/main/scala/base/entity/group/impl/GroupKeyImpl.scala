/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 1:10 PM
 */

package base.entity.group.impl

import base.entity.kv.PrivateHashKey
import base.entity.kv.impl.HashKeyImpl
import base.entity.group.GroupKey

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupKeyImpl(protected val key: PrivateHashKey) extends GroupKey with HashKeyImpl {

}
