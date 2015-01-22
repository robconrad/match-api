/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 11:47 AM
 */

package base.entity.group.kv.impl

import java.util.UUID

import base.entity.group.kv.{ GroupUserQuestionsTempKey, GroupUserQuestionsTempKeyService }
import base.entity.kv.Key.{ Pipeline, Id }
import base.entity.kv.impl.SetKeyServiceImpl
import base.entity.kv.{ Key, KeyId }
import base.entity.question.QuestionIdComposite

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupUserQuestionsTempKeyServiceImpl
    extends SetKeyServiceImpl[QuestionIdComposite, GroupUserQuestionsTempKey] with GroupUserQuestionsTempKeyService {

  def make(id: Id)(implicit p: Pipeline) = new GroupUserQuestionsTempKeyImpl(getKey(id), this)

  def make(groupId: UUID, userId: UUID)(implicit p: Pipeline) = make(KeyId(groupId + Key.PREFIX_DELIM + userId))

}
