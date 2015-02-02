/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/1/15 5:16 PM
 */

package base.entity.group.kv

import java.util.UUID

import base.entity.kv.{KeyPrefixes, KeyService, KeyServiceCompanion}

/**
 * {{ Describe the high level purpose of UserKeyService here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait GroupPhonesInvitedKeyService extends KeyService[UUID, GroupPhonesInvitedKey] {

  final val serviceManifest = manifest[GroupPhonesInvitedKeyService]

  final val prefix = KeyPrefixes.groupPhonesInvited

}

object GroupPhonesInvitedKeyService extends KeyServiceCompanion[GroupPhonesInvitedKeyService]
