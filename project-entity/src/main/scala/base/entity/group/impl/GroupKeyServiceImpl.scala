/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 1:09 PM
 */

package base.entity.group.impl

import base.entity.kv.Key.Id
import base.entity.kv.impl.{ HashKeyServiceImpl, PrivateHashKeyImpl }
import base.entity.group.{ GroupKey, GroupKeyService }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupKeyServiceImpl extends HashKeyServiceImpl[GroupKey] with GroupKeyService {

  def make(id: Id) = new GroupKeyImpl(new PrivateHashKeyImpl(getKey(id), this))

}
