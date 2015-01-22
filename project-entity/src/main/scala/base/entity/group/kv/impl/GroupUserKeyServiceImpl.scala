/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/21/15 9:41 PM
 */

package base.entity.group.kv.impl

import java.util.UUID

import base.entity.group.kv.{ GroupUserKey, GroupUserKeyService }
import base.entity.kv.Key.{ Id, Pipeline }
import base.entity.kv.impl.HashKeyServiceImpl
import base.entity.kv.{ Key, KeyId }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupUserKeyServiceImpl extends HashKeyServiceImpl[GroupUserKey] with GroupUserKeyService {

  def make(id: Id)(implicit p: Pipeline) = new GroupUserKeyImpl(getKey(id), this)

  def make(groupId: UUID, userId: UUID)(implicit p: Pipeline) = make(KeyId(groupId + Key.PREFIX_DELIM + userId))

}
