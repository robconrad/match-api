/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 4:12 PM
 */

package base.entity.group.kv.impl

import base.entity.group.kv.{ GroupUserKey, GroupUserQuestionsKey, GroupUserQuestionsKeyService }
import base.entity.kv.IdPair
import base.entity.kv.Key.Pipeline
import base.entity.kv.impl.{ IdPairKeyServiceImpl, IdPairTypedKeyServiceImpl, SetKeyServiceImpl }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupUserQuestionsKeyServiceImpl
    extends SetKeyServiceImpl[IdPair, GroupUserQuestionsKey]
    with GroupUserQuestionsKeyService
    with IdPairKeyServiceImpl[GroupUserQuestionsKey]
    with IdPairTypedKeyServiceImpl {

  def make(id: IdPair)(implicit p: Pipeline) = new GroupUserQuestionsKeyImpl(getKey(id), this)

}
