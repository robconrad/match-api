/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 12:49 PM
 */

package base.entity.group.kv.impl

import java.util.UUID

import base.entity.group.kv.{ GroupPairKey, GroupPairKeyService }
import base.entity.kv.Key.Pipeline
import base.entity.kv.impl.SimpleKeyServiceImpl

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupPairKeyServiceImpl extends SimpleKeyServiceImpl[(UUID, UUID), GroupPairKey] with GroupPairKeyService {

  def make(id: (UUID, UUID))(implicit p: Pipeline) = {
    val ordered = id._1.compareTo(id._2) match {
      case -1 => (id._1, id._2)
      case 1  => (id._2, id._1)
      case 0  => throw new RuntimeException("userA and userB are equal")
    }
    // todo these uuids are just getting stringified, lets store them as byte arrays
    new GroupPairKeyImpl(getKey(ordered), this)
  }

}
