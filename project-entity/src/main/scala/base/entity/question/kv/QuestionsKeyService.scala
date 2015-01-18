/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 9:23 AM
 */

package base.entity.question.kv

import base.entity.kv.{ KeyService, KeyServiceCompanion }

/**
 * {{ Describe the high level purpose of UserKeyService here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait QuestionsKeyService extends KeyService[QuestionsKey] {

  final def serviceManifest = manifest[QuestionsKeyService]

  final val CHANNEL = "questions"

}

object QuestionsKeyService extends KeyServiceCompanion[QuestionsKeyService]
