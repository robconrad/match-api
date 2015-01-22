/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 11:47 AM
 */

package base.entity.group.kv.impl

import java.util.UUID

import base.entity.group.kv.{ GroupUserQuestionsYesKey, GroupUserQuestionsYesKeyService }
import base.entity.kv.Key._
import base.entity.kv.{ Key, KeyId }
import base.entity.kv.impl.SetKeyServiceImpl
import base.entity.question.QuestionIdComposite

/**
 * {{ Describe the high level purpose of UserKeyServiceImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupUserQuestionsYesKeyServiceImpl
    extends SetKeyServiceImpl[QuestionIdComposite, GroupUserQuestionsYesKey] with GroupUserQuestionsYesKeyService {

  def make(id: Id)(implicit p: Pipeline) = new GroupUserQuestionsYesKeyImpl(getKey(id), this)

  def make(groupId: UUID, userId: UUID)(implicit p: Pipeline) = make(KeyId(groupId + Key.PREFIX_DELIM + userId))

}
