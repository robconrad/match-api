/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/18/15 1:28 PM
 */

package base.entity.user.kv

import java.util.UUID

import base.entity.kv.Key._
import base.entity.kv.{ KeyService, KeyServiceCompanion }

/**
 * {{ Describe the high level purpose of UserKeyService here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait UserUserLabelKeyService extends KeyService[UserUserLabelKey] {

  final def serviceManifest = manifest[UserUserLabelKeyService]

  final val CHANNEL = "userUserLabel"

  def make(ownerUserId: UUID, labelUserId: UUID)(implicit p: Pipeline): UserUserLabelKey

}

object UserUserLabelKeyService extends KeyServiceCompanion[UserUserLabelKeyService]
