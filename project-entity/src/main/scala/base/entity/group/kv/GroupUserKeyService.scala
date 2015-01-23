/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 4:07 PM
 */

package base.entity.group.kv

import base.entity.kv._

/**
 * {{ Describe the high level purpose of UserKeyService here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait GroupUserKeyService extends IdPairKeyService[GroupUserKey] {

  final val serviceManifest = manifest[GroupUserKeyService]

  final val prefix = KeyPrefixes.groupUser

}

object GroupUserKeyService extends KeyServiceCompanion[GroupUserKeyService]
