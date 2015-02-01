/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 1:17 PM
 */

package base.entity.user.kv

import java.util.UUID

import base.entity.kv.{ KeyPrefixes, KeyService, KeyServiceCompanion }

/**
 * {{ Describe the high level purpose of UserKeyService here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait UserPhonesInvitedKeyService extends KeyService[UUID, UserPhonesInvitedKey] {

  final val serviceManifest = manifest[UserPhonesInvitedKeyService]

  final val prefix = KeyPrefixes.userPhonesInvited

}

object UserPhonesInvitedKeyService extends KeyServiceCompanion[UserPhonesInvitedKeyService]