/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/11/15 8:49 PM
 */

package base.entity.user.kv.impl

import base.entity.kv.impl.SimpleKeyImpl
import base.entity.user.kv.{UserPhone, UserPhoneLabelKey}

/**
 * {{ Describe the high level purpose of UserKeyImpl here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
class UserPhoneLabelKeyImpl(val keyValue: UserPhone)
    extends SimpleKeyImpl[UserPhone, String]
    with UserPhoneLabelKey
