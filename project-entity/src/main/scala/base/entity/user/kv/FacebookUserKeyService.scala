/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/27/15 6:40 PM
 */

package base.entity.user.kv

import base.entity.kv.{ KeyPrefixes, KeyService, KeyServiceCompanion }

/**
 * {{ Describe the high level purpose of UserKeyService here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait FacebookUserKeyService extends KeyService[String, FacebookUserKey] {

  final val serviceManifest = manifest[FacebookUserKeyService]

  final val prefix = KeyPrefixes.facebookUser

}

object FacebookUserKeyService extends KeyServiceCompanion[FacebookUserKeyService]
