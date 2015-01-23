/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 4:12 PM
 */

package base.entity.group.kv.impl

import base.entity.group.kv.{ GroupPairKey, GroupUserKey, GroupUserKeyService }
import base.entity.kv.IdPair
import base.entity.kv.Key.Pipeline
import base.entity.kv.impl.{ IdPairKeyServiceImpl, HashKeyServiceImpl, IdPairTypedKeyServiceImpl }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupUserKeyServiceImpl
    extends HashKeyServiceImpl[IdPair, GroupUserKey]
    with GroupUserKeyService
    with IdPairKeyServiceImpl[GroupUserKey]
    with IdPairTypedKeyServiceImpl {

  def make(id: IdPair)(implicit p: Pipeline) = new GroupUserKeyImpl(getKey(id), this)

}
