/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 11:02 AM
 */

package base.entity.user.kv

import base.entity.kv._

/**
 * {{ Describe the high level purpose of UserKeyService here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait UserPhoneLabelKeyService extends KeyService[UserPhone, UserPhoneLabelKey] {

  final val serviceManifest = manifest[UserPhoneLabelKeyService]

  final val prefix = KeyPrefixes.userPhoneLabel

}

object UserPhoneLabelKeyService extends KeyServiceCompanion[UserPhoneLabelKeyService]
