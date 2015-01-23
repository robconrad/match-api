/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 4:46 PM
 */

package base.entity.group.kv.impl

import base.entity.group.kv.{ GroupPairKey, GroupPairKeyService }
import base.entity.kv.IdPair
import base.entity.kv.Key.Pipeline
import base.entity.kv.impl.{ IdPairKeyServiceImpl, SimpleKeyServiceImpl }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupPairKeyServiceImpl
    extends SimpleKeyServiceImpl[IdPair, GroupPairKey]
    with GroupPairKeyService
    with IdPairKeyServiceImpl[GroupPairKey] {

  def make(id: IdPair)(implicit p: Pipeline) = {
    val ordered = id.a.compareTo(id.b) match {
      case -1 => IdPair(id.a, id.b)
      case 1  => IdPair(id.b, id.a)
      case 0  => throw new RuntimeException("userA and userB are equal")
    }
    new GroupPairKeyImpl(getKey(ordered), this)
  }

}
