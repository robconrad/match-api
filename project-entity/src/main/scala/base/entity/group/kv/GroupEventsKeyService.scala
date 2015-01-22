/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 2:30 PM
 */

package base.entity.group.kv

import java.util.UUID

import base.entity.kv.{ KeyPrefixes, KeyService, KeyServiceCompanion }

/**
 * {{ Describe the high level purpose of UserKeyService here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait GroupEventsKeyService extends KeyService[UUID, GroupEventsKey] {

  final val serviceManifest = manifest[GroupEventsKeyService]

  final val prefix = KeyPrefixes.groupEvents

}

object GroupEventsKeyService extends KeyServiceCompanion[GroupEventsKeyService]
