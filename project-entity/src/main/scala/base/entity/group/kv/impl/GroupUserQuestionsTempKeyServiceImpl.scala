/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 4:56 PM
 */

package base.entity.group.kv.impl

import base.entity.group.kv.{ GroupUserQuestionsTempKey, GroupUserQuestionsTempKeyService }
import base.entity.kv.IdPair
import base.entity.kv.Key.Pipeline
import base.entity.kv.impl.{ IdPairKeyServiceImpl, SetKeyServiceImpl }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupUserQuestionsTempKeyServiceImpl
    extends SetKeyServiceImpl[IdPair, GroupUserQuestionsTempKey]
    with GroupUserQuestionsTempKeyService
    with IdPairKeyServiceImpl[GroupUserQuestionsTempKey] {

  def make(id: IdPair)(implicit p: Pipeline) = new GroupUserQuestionsTempKeyImpl(getKey(id), this)

}
