/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:34 PM
 */

package base.entity.group.kv.impl

import java.util.UUID

import base.entity.group.kv.{ GroupPairKey, GroupPairKeyService }
import base.entity.kv.Key.{ Pipeline, Id }
import base.entity.kv.impl.IdKeyServiceImpl
import base.entity.kv.{ Key, KeyId }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupPairKeyServiceImpl extends IdKeyServiceImpl[GroupPairKey] with GroupPairKeyService {

  def make(id: Id)(implicit p: Pipeline) = new GroupPairKeyImpl(getKey(id), this)

  def make(userA: UUID, userB: UUID)(implicit p: Pipeline) = {
    val ordered = userA.compareTo(userB) match {
      case -1 => (userA, userB)
      case 1  => (userB, userA)
      case 0  => throw new RuntimeException("userA and userB are equal")
    }
    make(KeyId(ordered._1 + Key.PREFIX_DELIM + ordered._2))
  }

}
