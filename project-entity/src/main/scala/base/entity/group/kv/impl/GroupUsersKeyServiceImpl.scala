/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 4:55 PM
 */

package base.entity.group.kv.impl

import base.entity.group.kv.{ GroupUsersKeyService, GroupUsersKey, GroupPairKey, GroupPairKeyService }
import base.entity.kv.Key.Id
import base.entity.kv.impl.{ SetKeyServiceImpl, IdKeyServiceImpl }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupUsersKeyServiceImpl extends SetKeyServiceImpl[GroupUsersKey] with GroupUsersKeyService {

  def make(id: Id) = new GroupUsersKeyImpl(getKey(id), this)

}
