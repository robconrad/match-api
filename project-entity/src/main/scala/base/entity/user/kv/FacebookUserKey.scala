/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 8:49 PM
 */

package base.entity.user.kv

import java.util.UUID

import base.entity.kv.{KeyPrefixes, SimpleKey}

/**
 * {{ Describe the high level purpose of UserKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait FacebookUserKey extends SimpleKey[String, UUID] {

  final val keyPrefix = KeyPrefixes.facebookUser

}
