/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/22/15 5:39 PM
 */

package base.entity.group.kv

import base.entity.kv._

/**
 * {{ Describe the high level purpose of UserKeyService here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait GroupPairKeyService extends IdPairKeyService[SortedIdPair, GroupPairKey] {

  final val serviceManifest = manifest[GroupPairKeyService]

  final val prefix = KeyPrefixes.groupPair

}

object GroupPairKeyService extends KeyServiceCompanion[GroupPairKeyService]
