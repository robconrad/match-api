/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/15/15 10:18 PM
 */

package base.entity.group.kv

import base.entity.kv.{ KeyService, KeyServiceCompanion }

/**
 * {{ Describe the high level purpose of UserKeyService here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait GroupEventsKeyService extends KeyService[GroupEventsKey] {

  final def serviceManifest = manifest[GroupEventsKeyService]

  final val CHANNEL = "groupEvents"

}

object GroupEventsKeyService extends KeyServiceCompanion[GroupEventsKeyService]
