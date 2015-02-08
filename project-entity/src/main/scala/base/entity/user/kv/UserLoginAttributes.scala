/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 2/7/15 3:53 PM
 */

package base.entity.user.kv

import org.joda.time.DateTime

/**
 * {{ Describe the high level purpose of UserLoginAttributes here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
case class UserLoginAttributes(phone: Option[String],
                               phoneVerified: Boolean,
                               name: UserNameAttributes,
                               lastLogin: Option[DateTime])
