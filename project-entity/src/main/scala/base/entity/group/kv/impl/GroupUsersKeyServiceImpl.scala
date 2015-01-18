/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:28 PM
 */

package base.entity.group.kv.impl

import base.entity.group.kv.{ GroupUsersKeyService, GroupUsersKey, GroupPairKey, GroupPairKeyService }
import base.entity.kv.Key._
import base.entity.kv.impl.{ SetKeyServiceImpl, IdKeyServiceImpl }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupUsersKeyServiceImpl extends SetKeyServiceImpl[GroupUsersKey] with GroupUsersKeyService {

  def make(id: Id)(implicit p: Pipeline) = new GroupUsersKeyImpl(getKey(id), this)

}
