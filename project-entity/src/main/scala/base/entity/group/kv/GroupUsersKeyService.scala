/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 3:59 PM
 */

package base.entity.group.kv

import base.entity.kv.{ KeyService, KeyServiceCompanion }

/**
 * {{ Describe the high level purpose of UserKeyService here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait GroupUsersKeyService extends KeyService[GroupUsersKey] {

  final def serviceManifest = manifest[GroupUsersKeyService]

  final val CHANNEL = "groupUsers"

}

object GroupUsersKeyService extends KeyServiceCompanion[GroupUsersKeyService]