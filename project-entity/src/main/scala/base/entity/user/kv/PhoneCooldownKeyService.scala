/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 2:30 PM
 */

package base.entity.user.kv

import base.entity.kv.{ KeyPrefixes, KeyService, KeyServiceCompanion }

/**
 * {{ Describe the high level purpose of UserKeyService here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait PhoneCooldownKeyService extends KeyService[String, PhoneCooldownKey] {

  final val serviceManifest = manifest[PhoneCooldownKeyService]

  final val prefix = KeyPrefixes.phoneCooldown

}

object PhoneCooldownKeyService extends KeyServiceCompanion[PhoneCooldownKeyService]
