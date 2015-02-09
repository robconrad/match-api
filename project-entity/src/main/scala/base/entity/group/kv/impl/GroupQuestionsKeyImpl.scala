/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 8:46 PM
 */

package base.entity.group.kv.impl

import java.util.UUID

import base.entity.group.kv.GroupQuestionsKey
import base.entity.kv.impl.SetKeyImpl
import base.entity.question.QuestionIdComposite

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class GroupQuestionsKeyImpl(val keyValue: UUID) extends SetKeyImpl[UUID, QuestionIdComposite] with GroupQuestionsKey
