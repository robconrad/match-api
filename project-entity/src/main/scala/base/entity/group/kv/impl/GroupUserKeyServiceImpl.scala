/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 12:54 PM
 */

package base.entity.group.kv.impl

import java.util.UUID

import base.entity.group.kv.{ GroupUserKey, GroupUserKeyService }
import base.entity.kv.Key.Pipeline
import base.entity.kv.impl.HashKeyServiceImpl

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupUserKeyServiceImpl extends HashKeyServiceImpl[(UUID, UUID), GroupUserKey] with GroupUserKeyService {

  def make(id: (UUID, UUID))(implicit p: Pipeline) = new GroupUserKeyImpl(getKey(id), this)

}
