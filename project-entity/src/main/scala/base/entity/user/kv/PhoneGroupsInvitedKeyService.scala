/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 1:47 PM
 */

package base.entity.user.kv

import base.entity.kv.{ KeyPrefixes, KeyServiceCompanion, SetKeyService }

/**
 * {{ Describe the high level purpose of UserKeyService here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait PhoneGroupsInvitedKeyService extends SetKeyService[String, PhoneGroupsInvitedKey] {

  final val serviceManifest = manifest[PhoneGroupsInvitedKeyService]

  final val prefix = KeyPrefixes.phoneGroupsInvited

}

object PhoneGroupsInvitedKeyService extends KeyServiceCompanion[PhoneGroupsInvitedKeyService]
