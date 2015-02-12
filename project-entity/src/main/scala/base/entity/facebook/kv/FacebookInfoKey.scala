/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 8:49 PM
 */

package base.entity.facebook.kv

import base.entity.facebook.FacebookInfo
import base.entity.kv.{KeyPrefixes, SimpleKey}

/**
 * {{ Describe the high level purpose of FacebookInfoKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait FacebookInfoKey extends SimpleKey[String, FacebookInfo] {

  final val keyPrefix = KeyPrefixes.facebookInfo

}
