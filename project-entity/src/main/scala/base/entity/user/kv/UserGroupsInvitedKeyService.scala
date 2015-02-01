/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/31/15 1:47 PM
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
trait UserGroupsInvitedKeyService extends KeyService[UUID, UserGroupsInvitedKey] {

  final val serviceManifest = manifest[UserGroupsInvitedKeyService]

  final val prefix = KeyPrefixes.userGroupsInvited

}

object UserGroupsInvitedKeyService extends KeyServiceCompanion[UserGroupsInvitedKeyService]
