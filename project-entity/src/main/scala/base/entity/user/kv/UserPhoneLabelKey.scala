/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 8:49 PM
 */

package base.entity.user.kv

import base.entity.kv.{KeyPrefixes, SimpleKey}

/**
 * {{ Describe the high level purpose of UserKey here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
trait UserPhoneLabelKey extends SimpleKey[UserPhone, String] {

  final val keyPrefix = KeyPrefixes.userPhoneLabel

}
