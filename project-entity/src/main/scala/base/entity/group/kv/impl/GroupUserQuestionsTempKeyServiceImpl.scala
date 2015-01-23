/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 5:42 PM
 */

package base.entity.group.kv.impl

import java.util.UUID

import base.entity.group.kv.{ GroupUserQuestionsTempKey, GroupUserQuestionsTempKeyService }
import base.entity.kv.{ OrderedIdPair, IdPair }
import base.entity.kv.Key.Pipeline
import base.entity.kv.impl.SetKeyServiceImpl

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupUserQuestionsTempKeyServiceImpl
    extends SetKeyServiceImpl[OrderedIdPair, GroupUserQuestionsTempKey]
    with GroupUserQuestionsTempKeyService {

  def make(a: UUID, b: UUID)(implicit p: Pipeline) = make(OrderedIdPair(a, b))

  def make(id: OrderedIdPair)(implicit p: Pipeline) = new GroupUserQuestionsTempKeyImpl(getKey(id), this)

}
