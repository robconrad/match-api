/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 9:23 AM
 */

package base.entity.group.kv.impl

import java.util.UUID

import base.entity.group.kv.{ GroupUserQuestionsKey, GroupUserQuestionsKeyService }
import base.entity.kv.Key.Id
import base.entity.kv.{ Key, KeyId }
import base.entity.kv.impl.SetKeyServiceImpl

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupUserQuestionsKeyServiceImpl
    extends SetKeyServiceImpl[GroupUserQuestionsKey] with GroupUserQuestionsKeyService {

  def make(id: Id) = new GroupUserQuestionsKeyImpl(getKey(id), this)

  def make(groupId: UUID, userId: UUID) = make(KeyId(groupId + Key.PREFIX_DELIM + userId))

}
