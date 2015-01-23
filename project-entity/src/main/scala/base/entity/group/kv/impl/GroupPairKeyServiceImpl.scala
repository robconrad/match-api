/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 5:37 PM
 */

package base.entity.group.kv.impl

import java.util.UUID

import base.entity.group.kv.{ GroupPairKey, GroupPairKeyService }
import base.entity.kv.Key.Pipeline
import base.entity.kv.SortedIdPair
import base.entity.kv.impl.SimpleKeyServiceImpl

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupPairKeyServiceImpl
    extends SimpleKeyServiceImpl[SortedIdPair, GroupPairKey]
    with GroupPairKeyService {

  def make(a: UUID, b: UUID)(implicit p: Pipeline) = make(SortedIdPair(a, b))

  def make(id: SortedIdPair)(implicit p: Pipeline) = new GroupPairKeyImpl(getKey(id), this)

}
