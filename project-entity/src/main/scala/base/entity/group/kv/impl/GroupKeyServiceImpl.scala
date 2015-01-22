/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/21/15 9:41 PM
 */

package base.entity.group.kv.impl

import base.entity.group.kv.{ GroupKey, GroupKeyService }
import base.entity.kv.Key.{ Id, Pipeline }
import base.entity.kv.impl.HashKeyServiceImpl

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupKeyServiceImpl extends HashKeyServiceImpl[GroupKey] with GroupKeyService {

  def make(id: Id)(implicit p: Pipeline) = new GroupKeyImpl(getKey(id), this)

}
