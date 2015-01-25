/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/25/15 1:40 PM
 */

package base.entity.facebook.kv

import base.entity.kv.{ KeyPrefixes, KeyService, KeyServiceCompanion }

/**
 * {{ Describe the high level purpose of UserKeyService here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait FacebookInfoKeyService extends KeyService[String, FacebookInfoKey] {

  final val serviceManifest = manifest[FacebookInfoKeyService]

  final val prefix = KeyPrefixes.facebookInfo

}

object FacebookInfoKeyService extends KeyServiceCompanion[FacebookInfoKeyService]
