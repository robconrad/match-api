/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 5:40 PM
 */

package base.entity.group.kv

import base.entity.kv.{ OrderedIdPair, IdPairKeyService, KeyPrefixes, KeyServiceCompanion }

/**
 * {{ Describe the high level purpose of UserKeyService here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait GroupUserQuestionsTempKeyService extends IdPairKeyService[OrderedIdPair, GroupUserQuestionsTempKey] {

  final val serviceManifest = manifest[GroupUserQuestionsTempKeyService]

  final val prefix = KeyPrefixes.groupUserQuestionsTemp

}

object GroupUserQuestionsTempKeyService extends KeyServiceCompanion[GroupUserQuestionsTempKeyService]
