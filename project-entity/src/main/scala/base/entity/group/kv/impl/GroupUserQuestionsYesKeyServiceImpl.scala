/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 4:56 PM
 */

package base.entity.group.kv.impl

import base.entity.group.kv.{ GroupUserQuestionsYesKey, GroupUserQuestionsYesKeyService }
import base.entity.kv.IdPair
import base.entity.kv.Key._
import base.entity.kv.impl.{ IdPairKeyServiceImpl, SetKeyServiceImpl }

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupUserQuestionsYesKeyServiceImpl
    extends SetKeyServiceImpl[IdPair, GroupUserQuestionsYesKey]
    with GroupUserQuestionsYesKeyService
    with IdPairKeyServiceImpl[GroupUserQuestionsYesKey] {

  def make(id: IdPair)(implicit p: Pipeline) = new GroupUserQuestionsYesKeyImpl(getKey(id), this)

}
