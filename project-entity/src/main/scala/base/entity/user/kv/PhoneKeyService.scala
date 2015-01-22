/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 12:49 PM
 */

package base.entity.user.kv

import base.entity.kv.{ KeyService, KeyServiceCompanion }

/**
 * {{ Describe the high level purpose of UserKeyService here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait PhoneKeyService extends KeyService[String, PhoneKey] {

  final val serviceManifest = manifest[PhoneKeyService]

  final val CHANNEL = "phone"

}

object PhoneKeyService extends KeyServiceCompanion[PhoneKeyService]
