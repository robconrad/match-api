/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/7/15 3:34 PM
 */

package base.entity.group.kv.impl

import java.util.UUID

import base.entity.group.kv.GroupUserQuestionsTempKey
import base.entity.kv.OrderedIdPair
import base.entity.kv.impl.SetKeyImpl
import base.entity.question.QuestionIdComposite

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupUserQuestionsTempKeyImpl(val keyValue: OrderedIdPair)
    extends SetKeyImpl[OrderedIdPair, QuestionIdComposite]
    with GroupUserQuestionsTempKey {

  def this(keyValue: (UUID, UUID)) =
    this(OrderedIdPair(keyValue._1, keyValue._2))

}
