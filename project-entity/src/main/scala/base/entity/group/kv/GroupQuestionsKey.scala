/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/8/15 9:24 PM
 */

package base.entity.group.kv

import java.util.UUID

import base.entity.kv.{ KeyPrefixes, SetKey }
import base.entity.question.QuestionIdComposite

/**
 * {{ Describe the high level purpose of UserKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait GroupQuestionsKey extends SetKey[UUID, QuestionIdComposite] {

  final val keyPrefix = KeyPrefixes.groupQuestions

}
