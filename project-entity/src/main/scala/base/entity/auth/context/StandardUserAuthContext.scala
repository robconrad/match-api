/*
 * Copyright (c) 2015 Robert Conrad - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Last modified by rconrad, 1/8/15 5:15 PM
 */

package base.entity.auth.context

import base.entity.perm.PermSetGroups
import base.entity.user.User

/**
 * {{ Describe the high level purpose of StandardUserAuthContext here. }}
 * {{ Include relevant details here. }}
 * {{ Do not skip writing good doc! }}
 * @author rconrad
 */
case class StandardUserAuthContext(_user: User) extends UserAuthContext {

  val user = Option(_user)

  val perms = PermSetGroups.user

}

