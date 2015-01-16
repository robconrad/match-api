/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 4:55 PM
 */

package base.entity.group.kv.impl

import java.util.UUID

import base.entity.group.kv.{ GroupPairKey, GroupPairKeyService }
import base.entity.kv.Key.Id
import base.entity.kv.impl.IdKeyServiceImpl
import base.entity.kv.{ Key, KeyId }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupPairKeyServiceImpl extends IdKeyServiceImpl[GroupPairKey] with GroupPairKeyService {

  def make(id: Id) = new GroupPairKeyImpl(getKey(id), this)

  def make(userA: UUID, userB: UUID) = {
    val ordered = userA.compareTo(userB) match {
      case -1 => (userA, userB)
      case 1  => (userB, userA)
      case 0  => throw new RuntimeException("userA and userB are equal")
    }
    make(KeyId(ordered._1 + Key.PREFIX_DELIM + ordered._2))
  }

}
