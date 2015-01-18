/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 2:58 PM
 */

package base.entity.group.kv

import java.util.UUID

import base.entity.kv.Key._
import base.entity.kv.{ KeyService, KeyServiceCompanion }

/**
 * {{ Describe the high level purpose of UserKeyService here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait GroupUserQuestionsYesKeyService extends KeyService[GroupUserQuestionsYesKey] {

  final val serviceManifest = manifest[GroupUserQuestionsYesKeyService]

  final val CHANNEL = "groupUserQuestionsYes"

  def make(groupId: UUID, userId: UUID)(implicit p: Pipeline): GroupUserQuestionsYesKey

}

object GroupUserQuestionsYesKeyService extends KeyServiceCompanion[GroupUserQuestionsYesKeyService]
